import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import VideoUpload from './components/VideoUpload'
import { TextInput, Toast ,Button } from 'flowbite-react'
import { Toaster } from 'react-hot-toast'
import VideoPlayer from './components/VideoPlayer'

function App() {
  const [count, setCount] = useState(0);
  // count [fieldValue , setFieldValue] =useState(null);
  const [fieldValue, setFieldValue] = useState(null);
  const [videoId , setvideoId] = useState("4e78c6e0-1b4b-4bec-bfb8-4b24a38ed1aa");

  function playVideo(videoId){
    setvideoId(videoId);
  }
  
  return (
    <>
    <Toaster/>
      <div className="flex justify-center flex-col items-center space-y-9 py-9">
        <h1 className="text-3xl font-bold text-gray-800 dark:text-gray-100">
           Video streaming application
        </h1>


      <div className="flex mt-14 w-full space-x-2 justify-between">
      
      <div className="w-1/2">
          <h1 className='text-white text-center mt-2'>Playing Vedio</h1>
          {/* <video 
          style={{
            width:500
          }
          }
          // src={`http://localhost:8082/api/v1/videos/stream/range/${videoId}`}
          src="http://localhost:8082/api/v1/videos/4e78c6e0-1b4b-4bec-bfb8-4b24a38ed1aa/master.m3u8"
           controls>
           </video> */}

            

           <div>
            <VideoPlayer src={`http://localhost:8082/api/v1/videos/${videoId}/master.m3u8`}></VideoPlayer>
           </div>
      </div>

      <div className="w-1/2">
        <VideoUpload/>
        </div>
      </div>
      
      <div className="my-4 flex space-x-4">
              <TextInput
              onClick={(event)=>{
                setFieldValue(event.target.value);
              }}
              // onChange={(event) => {
              //   setFieldValue(event.target.value);
              // }}
               name="video_id_field"
              placeholder="Enter Video id here"
              ></TextInput>
              <Button
               onClick={() => {
                setvideoId(fieldValue);
               }}>Play</Button>
            </div>

      </div>
    </>
  );
}

export default App
