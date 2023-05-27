# AquaPure : Water Quality Assessment App
## Description
  AquaPure is a cutting-edge Android application designed to provide users with a quick and reliable method of assessing the drinkability of water using machine learning (ML) models , APIs and other  algorithms. This app empowers users to make informed decisions about the water they consume by leveraging the power of technology and crowd-sourced data. Additionally, AquaPure offers access to recent reports of water assessments in nearby areas, enabling users to stay informed about the water quality around them.


![1-merged](https://github.com/AnuragRanjan2003/MediConnect/assets/100191258/039e62e4-e2ba-4643-8781-b893d93953b8)

## Key Features
   1. **Water Assessment :** Users can capture a photo of water using their device's camera within the app. AquaPure utilizes ML models ,APIS and algorithms to analyze the image, assessing various parameters to determine the drinkability of the water. The app considers factors such as color, turbidity, clarity, and presence of contaminants to provide an evaluation.
  
   2. **Drinkability Analysis :** Based on the analysis of the water image, AquaPure provides an assessment of the drinkability, indicating whether the water is deemed safe for consumption or if caution is advised. The app employs a reliable ML model trained on a comprehensive dataset of water quality parameters.
    
   3. **Recent Reports :** AquaPure maintains a database of water assessments conducted by users in nearby areas. Users can access this information to explore recent reports and gain insights into the water quality in their vicinity. The reports may include details such as location and assessment results.
    
   4. **Authority Engagement :** The complaints registered via AquaPure in the database are visible to the concered nearby authorities and they can take action on the same.
   
   
   

 ## Working 
  1. **Location :** The application keeps tracking the users position using mobile GPS . This location is available as an observable Livedata.
  
  2. **Quality in Area :** User can then see the quality of water near their location as a quality index. A list of latest complaints is also visible to the user so they can get an overview of the problem.These include the main contaminant , location and date.  
  
  3. **Scan :** The users can upload an image in from thei gallery or click one to upload. This image will be uploaded to the Firebase Storage and then the url will be sent to the ColoursAPI to get the chromatic information of the image.
  
  4. **Image Analysis :** The dominant color RGB and hue are obtained using ColorAPI. This information is used to analyse the problem with the water ( Algae, Dirty ) as an index of Algae ans Dirty. The final verdict of weter the water is drinkable is generated using a ML model generated by teachable Machine service. These details are shown to the user in the UI.
  
  5. **Reporting :** If the warer is not drinkable the user is given the option to report. This report includes the location, quality index, problem( Algae, Dirty ). This report is saved in Firebase Realtime Database. They can be viewed by the concerned authorities.
  

## APIs used
 1. ColoursAPI - API to get the information about colour content ( Dominant color RGB , Hue etc. ) fo an image.
 
## Other Resources
  1. Teachable Machine - A website that generates simple ML models that can classify images. 

## TechStack
 1. Kotlin
 2. XML
 3. Retrofit
 4. MVVM
 5. Kotlin coroutines
 6. Firebase( Authentication, RealtimeDatabase , FirebaseStorage )
 7. Location
 8. Navigation

  ###  Other Dependencies
 1. Glide - Easy Image Loading 
 2. Lottie Animation - Show lottie animations in android
 3. Loading Shimmer - Facebook like loading shimmer
 4. Dexter - Permission handelling
 
 