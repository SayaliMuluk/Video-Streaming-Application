package com.stream.app.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stream.app.AppConstants;
import com.stream.app.entities.Video;
import com.stream.app.playload.CustomeMessage;
import com.stream.app.services.VideoService;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
public class VideoController {

	private VideoService videoService;
	public VideoController(VideoService videoService) {
		super();
		this.videoService = videoService;
	}

	// video upload ke liye 
	@PostMapping
	public ResponseEntity<?> create( //CustomeMessage at the place of ?

			
			@RequestParam("file")MultipartFile file,
			@RequestParam("title") String title,
			@RequestParam("description") String description
			){
		
		
		Video video = new Video();
		video.setTitle(title);
		video.getDescription();
		video.setVideoId(UUID.randomUUID().toString());
		
		Video savedVideo = videoService.save(video, file);
		
		if(savedVideo != null)
		{
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(video);
		}
		else
		{
//			return ResponseEntity
//					.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(CustomeMessage.builder()
//							.message("video not uploaded")
//							.success(false)
//							.build()
//							);
     System.out.println("builder method is not working n sts");
		}
		return null;
		
}
	
	//get all videos
	@GetMapping
	public List<Video> getAll(){
		return videoService.getAll();
	}
	
	
	//stream video
	@GetMapping("/stream/{videoId}")
	public ResponseEntity<Resource> stream(
			@PathVariable String videoId
	)
	{
		Video video = videoService.get(videoId);
		
		String contentType = video.getContentType();
		String filePath = video.getFilePath();
		
		Resource  resource = new FileSystemResource(filePath);
		
		if(contentType == null)
		{
			contentType = "application/octet-stream";
		}
		
		
		return (ResponseEntity<Resource>) ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.body(resource)
				;
	}
	
	//stream video in chunks 01-09-29-2024
	
	@GetMapping("/stream/range/{videoId}")
	public ResponseEntity<Resource> streamVideoRange(
			@PathVariable String videoId,
			@RequestHeader(value = "Range" ,required = false) String range
			)
	{
		System.out.println(range);
		
		Video video = videoService.get(videoId);
		Path path = Paths.get(video.getFilePath());
		
		Resource resource = new FileSystemResource(path);
		
		String contentType = video.getContentType();
		
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		
		//for the file lenght
		
		long fileLength = path.toFile().length();
		
		//same code as agodaercha code
		if (range == null) {
			
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.body(resource);
		}
		
		//calculating start and nd range for video byte
//   **************** my code start**************************	
//		long rangeStart;
		
//		long rangeEnd; 
		
//		String[] ranges = range.replace("bytes=", " ").split("-");
//		rangeStart = Long.parseLong(ranges[0]);
//		
//		if (range.length() > 1) {
//			rangeEnd = Long.parseLong(ranges[1]);
//		}else
//		{
//			rangeEnd = fileLength - 1;
//		}
//		
//		
//		if (rangeEnd > fileLength - 1 ) {
//			rangeEnd = fileLength - 1;
//		}
//		   ****************end**************************	
		
		
//		   **************** chat gpt  code start**************************	
		if (range == null || range.isEmpty()) {
		    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
		}

		String[] ranges = range.replace("bytes=", "").split("-");
		long rangeStart = Long.parseLong(ranges[0].trim());

//		long rangeEnd;
//		if (ranges.length > 1 && !ranges[1].trim().isEmpty()) {
//		    rangeEnd = Long.parseLong(ranges[1].trim());
//		} else {
//		    rangeEnd = fileLength - 1;
//		}
//
//		if (rangeEnd > fileLength - 1) {
//		    rangeEnd = fileLength - 1;
//		}
//		   **************** chat gpt code ends**************************	
		
		long rangeEnd = rangeStart + AppConstants.CHUNK_SIZE;
		
		if(rangeEnd >= fileLength)
		{
			rangeEnd = fileLength - 1;
		}
		
		System.out.println("start range :"+ rangeStart);
		System.out.println("end range :"+ rangeEnd);
		
		InputStream inputStream;
		
		try {
			
			inputStream = Files.newInputStream(path);
			inputStream.skip(rangeStart);
			long contentLength = rangeEnd - rangeStart+1;
			
			byte[] data = new byte[(int) contentLength];
			int read = inputStream.read(data, 0, data.length);
			System.out.println("read (number of bytes ): "+read);
			
			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Range", "bytes="+rangeStart+"-"+rangeEnd+"/"+fileLength);
			headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
			headers.add("Cache-Control", "no-cache , no-store , must-revalidation");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("X-Content-Type-Options", "nosniff");
			headers.setContentLength(contentLength);
			
			
			
			return ResponseEntity
					.status(HttpStatus.PARTIAL_CONTENT)
					.headers(headers)
					.contentType(MediaType.parseMediaType(contentType))
					.body(new ByteArrayResource(data));
			
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		
	
	}
	
	
	// date 02-09-2024

	///serve hls playlist
	
	
	//master.m2u8
	@Value("${files.video.hsl}")
	private String HSL_DIR;
	@GetMapping("/{videoId}/master.m3u8")
	public ResponseEntity<Resource> serverMasterFile(
			@PathVariable String videoId
			)
	{
		
		//creating path
		
		Path path = Paths.get(HSL_DIR, videoId , "master.m3u8");
		
		System.out.println(path);
		
		if(!Files.exists(path))
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		Resource resource = new FileSystemResource(path);
		
		return ResponseEntity
				.ok()
				.header(
						HttpHeaders.CONTENT_TYPE,"application/vnd.apple.mpegurl"
						)
				.body(resource);
		
	}
	
	
	//serve the segments
	
	@GetMapping("/{videoId}/{segment}.ts")
	public ResponseEntity<Resource> serveSegments(
			@PathVariable String videoId,
			@PathVariable String segment
			)
	{
		
		//crete path foe segments
		
		Path path = Paths.get(HSL_DIR, videoId, segment+".ts");
		
		if (!Files.exists(path)) {
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		Resource resource = new FileSystemResource(path);
		
		
		return ResponseEntity
				.ok()
				.header(
						HttpHeaders.CONTENT_TYPE,"video/mp2t"
						)
				.body(resource);
	}
	
}




