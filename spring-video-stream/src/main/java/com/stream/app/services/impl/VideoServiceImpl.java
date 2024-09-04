package com.stream.app.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.entities.Video;
import com.stream.app.repositories.VideoRepository;
import com.stream.app.services.VideoService;

import jakarta.annotation.PostConstruct;

@Service
public class VideoServiceImpl implements VideoService {


	@Value("${files.video}")
	String DIR;
	
	@Value("${files.video.hsl}")
	String HSL_DIR;
	
	private VideoRepository videoRepository;
	
	public VideoServiceImpl(VideoRepository videoRepository) {
		super();
		this.videoRepository = videoRepository;
	}
	
	
	@PostConstruct
	public void init()
	{
		
		File file = new File(DIR);
		
		try {
			Files.createDirectories(Paths.get(HSL_DIR));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		File file1 = new File(HSL_DIR);
//		
//		if (!file1.exists()) {
//			file1.mkdir();
//			System.out.println(" HSL Folder created");
//		}
		
		if(!file.exists())
		{
			file.mkdir();
			System.out.println("Folder created");
		}
		else
		{
			System.out.println("folder already created");
		}
	}
	
	@Override
	public Video save(Video video, MultipartFile file)
	{
	
		try {
			//original file name
			String filename = file.getOriginalFilename();
			String contentType = file.getContentType();
			
			InputStream inputStream = file.getInputStream();
			
			//file path 		
			String cleanFileName =StringUtils.cleanPath(filename);
			//folder path		
			String cleanFolder =StringUtils.cleanPath(DIR);
			//folder path with filename
			Path path = Paths.get(cleanFolder, cleanFileName);

			System.out.println(contentType);
			System.out.println(path);

			
			//copy file to the folder
			
			Files.copy(inputStream, path,StandardCopyOption.REPLACE_EXISTING);
			
			//video meta data
			video.setContentType(contentType);
			video.setFilePath(path.toString());
			
			 videoRepository.save(video);
			
			//processing video
			processVideo(video.getVideoId());
			
			//metadata save
		return video;
			
			
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
	}

	
	
	@Override
	public Video get(String videoId) {
		System.out.println("Fetching video with ID: " + videoId);
		Video video = videoRepository.findById(videoId).orElseThrow(() ->
		
		new RuntimeException("video not found"));
		
		return video ;
	}

	@Override
	public Video getByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> getAll() {
 		
		return videoRepository.findAll();
	}             


//	@Override
//	public String processVideo(String videoId , MultipartFile file) {
//		
//	    Video video = this.get(videoId);
//		String filePath = video.getFilePath();
		
		//path where to store data:
		
//		Path videoPath = Paths.get(filePath);
		
//		String output360p = HSL_DIR+videoId+"/360p/";
//		String output720p = HSL_DIR+videoId+"/720p/";
//		String output1080p = HSL_DIR+videoId+"/1080p/";
//		
		
//		System.out.println("360p Path: " + output360p);
//	    System.out.println("720p Path: " + output720p);
//	    System.out.println("1080p Path: " + output1080p);
//		
//		try {
//			Files.createDirectories(Paths.get(output360p));
//			Files.createDirectories(Paths.get(output720p));
//			Files.createDirectories(Paths.get(output1080p));
			
			
			//ffmpeg command
			
//			Path outputPath = Paths.get(HSL_DIR,videoId);
//			
//			Files.createDirectories(outputPath);
//			
//			String ffmpegCmd = String.format(
//						"ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\" \"%s/master.m3u8\" ",
//						videoPath,outputPath,outputPath
//					);
//			
//			System.out.println(ffmpegCmd);
			
			//file this command
			
//			System.out.println("this is the line before process builder");
//			ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash","-c", ffmpegCmd);
//			processBuilder.inheritIO();
//			 processBuilder.redirectErrorStream(true);
//			System.out.println("here the process start");
//			Process process = processBuilder.start();
//			int exit = process.waitFor();
//			
//			if(exit != 0)
//			{
//				throw new RuntimeException("video processing failed!!");
//			}
//			
//			return videoId;
//
//			
//	} 
//		catch (IOException ex) {
//			throw new RuntimeException("Video processing fail !!!");
//		}
//		catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			throw new RuntimeException(e);
//		}
//}
		
	@Override
	public String processVideo(String videoId) {
	    Video video = this.get(videoId);
	    String filePath = video.getFilePath();
	    Path videoPath = Paths.get(filePath);

	    try {
	        Path outputPath = Paths.get(HSL_DIR, videoId);
	        Files.createDirectories(outputPath);

	        String ffmpegCmd = String.format(
	            "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\" \"%s/master.m3u8\"",
	            videoPath, outputPath, outputPath
	        );

	        System.out.println(ffmpegCmd);

	        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);
	        processBuilder.redirectErrorStream(true); // Redirect error stream
	        Process process = processBuilder.start();

	        // Capture and log the output
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                System.out.println(line);
	            }
	        }

	        int exit = process.waitFor();
	        if (exit != 0) {
	            throw new RuntimeException("Video processing failed!!");
	        }

	        return videoId;

	    } catch (IOException ex) {
	        throw new RuntimeException("Video processing failed!!!", ex);
	    } catch (InterruptedException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	

}
