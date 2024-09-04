import React, { useState } from "react"

import videoLogo from '../assets/upload.png'
import { Button, Card, FileInput, Label, Textarea, TextInput ,Progress, Alert} from "flowbite-react"
import axios from "axios";
import toast from "react-hot-toast";

function VideoUpload(){

    const [selectedFile, setSelectedFile] = useState(null);
    const [meta,setMeta] = useState({title:"" , description:""})
    const [progress, setProgress] = useState(0);
    const [uploading , setUploading] = useState(false);
    const [message , setMessage] = useState("");

    function handleFileChange(event) {
      console.log(event.target.files[0]);
       setSelectedFile(event.target.files[0]);
    }

    function formFieldChange(event)
    {
        // console.log(event.target.name);
        // console.log(event.target.value);
        setMeta({
            ...meta,
            [event.target.name]:event.target.value
        })
    }


    function handleForm(fromEvent){
        fromEvent.preventDefault();

        if(!selectedFile)
        {
            alert("Select File !!!");
            return;
        }
        //submit the file to server
        saveVideoToServer(selectedFile,meta);

        // console.log(meta);
        // console.log(fromEvent.target.title.value);
        // console.log(fromEvent.target.description.value);
        // console.log("button clicked");
        // console.log(selectedFile);
    }


    function resetForm(){
        setMeta({
            title:"",
            description:"",
        });
        setSelectedFile(null);
        setUploading(false);
        // setMessage("");
    }


     //submit the file to server
     async function saveVideoToServer(video,videoMetaData){
        setUploading(true);


        //api call
       try{

        let formData = new FormData()
        formData.append("title" , videoMetaData.title);
        formData.append("description" , videoMetaData.description);
        formData.append("file",selectedFile);

        let response = await axios.post("http://localhost:8082/api/v1/videos", formData ,{
            headers:{
                "Content-Type":"multipart/form-data",
            },
            onUploadProgress:(progressEvent)=>{
                const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                console.log(progress);
                setProgress(progress);
            },
        } );

        console.log(response);
        setProgress(0);

        setMessage("File uploaded.. " +response.data.videoId)
        setUploading(false);
        toast.success("file uploaded sucessfully !!");
        resetForm();
       }
       catch(error)
       {
        console.log(error);
        setMessage("Error in Uploading file");
        setUploading(false);
        toast.error("file not uploaded !!");
       }
     }




    return (
        <div className="text-white">
            <Card className="flex flex-col items-center justify-center ">
                <h1>
                    Upload Videos
                </h1>
                <form noValidate onSubmit={handleForm} className="flex flex-col space-y-5 ">


                 <div>
                    <div className="mb-2 block">
                    <Label htmlFor="file-upload" value="Video Title" />
                     </div>
                    <TextInput 
                    value={meta.title}
                    onChange={formFieldChange} 
                    name="title" 
                    placeholder="enter title"></TextInput>
                 </div>

                    <div className="max-w-md">
                        <div className="mb-2 block">
                         <Label htmlFor="comment" value="vedio description" />
                     </div>
                         <Textarea 
                         value={meta.description}
                         onChange={formFieldChange} 
                         id="comment" 
                         name="description" 
                         placeholder="write video description"
                          required rows={4} />
                    </div>

                    

                  <div className="flex items-center space-x-5 justify-center">
                        <div className="shrink-0">
                            <img className="h-16 w-16 object-cover" 
                                src={videoLogo} 
                                alt="Current profile photo" />
                        </div>
                        <label className="block">
                            <span className="sr-only">Choose Video File</span>
                            <input 
                            
                            name="file"
                            onChange={handleFileChange}
                            type="file" className="block w-full text-sm text-slate-500
                            file:mr-4 file:py-2 file:px-4
                            file:rounded-full file:border-0
                            file:text-sm file:font-semibold
                            file:bg-violet-50 file:text-violet-700
                            hover:file:bg-violet-100
                            "/>
                        </label>
                    </div>

                    <div className="">
                            <Progress 
                            color="green"
                                hidden={!uploading}
                                progress={progress}
                                textLabel="Uploading..." 
                                size="lg"
                                labelProgress
                                labelText 
                            />
                    </div>

                    <div className="">
                            {message &&
                            <Alert color={"success"} 
                            rounded withBorderAccent
                            onDismiss={() => { setMessage("")}}
                            >
                            <span className="font-medium">Success Alert</span>
                            {message}
                        </Alert>}
                    </div>


                    <div className="flex justify-center">
                        <Button disabled={uploading} type="submit">Submit</Button>
                    </div>
               </form>

{/* this is old uploading button */}
{/*  <div  className="flex justify-center">
  <Button onClick={handleForm}>Upload</Button>
  </div> */}

</Card>
</div>
    )
}

export default VideoUpload