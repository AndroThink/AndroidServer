# AndroidServer

Java android library for creating a Multi-Threaded Http Server on android .

## Main Features
   - Dynamic routes .
   - Support Http methods : GET ,POST ,PUT and DELETE .
   - Handle body payload : plain text , json , formdata and xformurlencoded .
   - Support response with : plain text , json , html and files .

## Installation

### In Project Level :

```groovy
allprojects {
   repositories {
	...
	maven { url 'https://www.jitpack.io' }
   }
}
```

### In App Module Level :
    Note that the minSdkVersion must be >= 16

```groovy
dependencies {
   ...
   implementation 'com.github.AndroThink:AndroidServer:{version}'
}
```

## Usage

### Note :

Default GET routes :
   - /images/ + filename
   - /sounds/ + filename

   These two routes response with the image or sound file that has the filename {filename} 
   that exist in assets folder or response with 404 not found if not exist .

```java
// Create routes ..
List<Route> routes = new ArrayList<>();

// Add not authorized route ..
routes.add(new Route("/",ServerHelper.METHOD.GET, new RouteCallBack() {
    @Override
    public void onRequested(Request request, ResponseHandler responseHandler)throws IOException {
        responseHandler.sendJsonResponse("{\"status\":true,\"data\":\"Demo Response .\"}");
    }
}));

// Add authorized route ..
routes.add(new Route("/test",ServerHelper.METHOD.GET, true, new RouteCallBack() {
    @Override
    public void onRequested(Request request, ResponseHandler responseHandler)throws IOException {
        String authKey = request.getApiKey();
        responseHandler.sendJsonResponse("{\"status\":true,\"data\":\"AthuKey " + authKey + ".\"}");
    }
}));

// Response with html file ..
// Add file to Assets folder then pass is path .
routes.add(new Route("/html",ServerHelper.METHOD.GET, new RouteCallBack() {
    @Override
    public void onRequested(Request request, ResponseHandler responseHandler)throws IOException {
        responseHandler.sendHtmlFileResponse(ServerHelper.RESPONSE_CODE.OK, "html/index.html");
    }
}));

// You can add placeholders inside your html and send its value within the response .
// EX : simple html file called 500.html all text = error_place_holder inside the file
// will be replaced with the text provided and so on ..
routes.add(new Route("/htmlplaceholder",ServerHelper.METHOD.GET, new RouteCallBack() {
    @Override
    public void onRequested(Request request, ResponseHandler responseHandler)throws IOException {

        Map<String, String> placeHolders = new HashMap<>();
        placeHolders.put("error_place_holder", "The text to be rendered");
        responseHandler.sendHtmlFileResponse(ServerHelper.RESPONSE_CODE.NOT_FOUND,
         "html/500.html", placeHolders);
    }
}));

// and so on with sound => responseHandler.sendSoundResponse()
// and so on with Images => responseHandler.sendImageResponse()

// Create Server instance ..
// You can pass ServerCallBack as ServerUtils.getInstance(routes,port,callBack) as server callback .

ServerUtils serverUtils = ServerUtils.getInstance(routes,8080);

// To Start Server
if (serverUtils != null)
    serverUtils.start(Context);

// To Stop Server
if (serverUtils != null)
    serverUtils.stop();

```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
                 Copyright 2019 AndroThink .
                 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.