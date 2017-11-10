<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="layout" content="main">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Videos</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/flowplayer/7.2.1/skin/skin.min.css">

    <style>
        body { padding-top: 0px !important; }
    </style>
</head>

<body>
<div class="flowplayer" data-swf="/flowplayer.swf" data-ratio="0.4167">
    <video>
        <!--
        <source type="video/webm" src="https://edge.flowplayer.org/bauhaus.webm">
        <source type="video/mp4" src="https://edge.flowplayer.org/bauhaus.mp4">
        -->
        <source type="application/x-mpegurl" src="${src}">
    </video>
</div>

</body>
</html>

<content tag="scripts">
    <!-- flowplayer and jQuery 3 = pain -->
    <script src="https://code.jquery.com/jquery-1.11.2.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/flowplayer/7.2.1/flowplayer.min.js"></script>
    <!-- load the latest version of the hlsjs plugin -->
    <script src="//releases.flowplayer.org/hlsjs/flowplayer.hlsjs.min.js"></script>
</content>
