
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="layout" content="main">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Videos</title>

</head>

<body>

<main role="main" class="container-fluid" id="video-main" v-cloak>
    <div class="row">
        <div class="col-xs-12 col-sm-12 col-md-6 offset-md-3">
            <h1>μ2b</h1>
            <div class="card">
                <div class="card-body">
                    <button class="btn btn-primary col-lg-12" id="upload-btn" disabled><i class="fa fa-plus"></i><span>Working...</span></button>
                    <div class="col-lg-12 instructions" id="upload-instructions">You can upload {{ maxVideos - videos.length }} more videos of up to {{ maxFilesize }}. MP4 is best, but any common video format should work.</div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-12 col-sm-12 col-md-6 offset-md-3" v-if="transcodingVideos.length > 0">
            <bp:alert intent="info">
                {{ transcodingVideos.length }} video{{ transcodingVideos.length > 1 ? 's' : '' }} {{ transcodingVideos.length > 1 ? 'are' : 'is' }} processing: {{ transcodingVideos.length > 1 ? 'they' : 'it' }}'ll automagically appear below when ready.
            </bp:alert>
        </div>
    </div>
    <div id="video-card-holder">
        <div class="row" v-for="video in transcodedVideos">
            <div class="col-xs-12 col-sm-12 col-md-6 offset-md-3">
                <div class="card">
                    <div class="card-body">
                        <p><img v-bind:src="video.thumbUri" width="192"/></p>
                        <div>
                            <button v-bind:data-video-id="video.id" class="btn btn-sm btn-primary video-play-btn"><i class="fa fa-play"></i> Play</button>
                            <a v-bind:href="video.downloadUri" class="btn btn-sm btn-primary"><i class="fa fa-download"></i> Download</a>
                            <button v-bind:data-video-id="video.id" class="btn btn-sm btn-danger video-remove-btn"><i class="fa fa-trash"></i> Remove</button>
                        </div>
                        <div class="metadata">{{ video.duration}}, originally {{ video.resolution }}</div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</main>

</body>
</html>

<content tag="scripts">
    <asset:javascript src="global/modules/plupload/plupload.full.min.js" />
    <asset:javascript src="page-specific/index.js"/>
</content>
