/**
 * Not using a framework, but keeping things kind of tidy with simple MVC
 */
function VideoController(config) {
    var self = this

    // current pluploader instance
    self.uploader

    // videos
    self.videos = []
    self.transcodedVideos = []
    self.transcodingVideos = []
    self.transcodingIntervals = {}

    // settings
    self.playbackSupported = false
    self.maxVideos = config.maxVideos
    self.maxFilesize = config.maxFilesize
    self.transcodingPollInterval = 1 * 3000
    self.endpoints = {
        video: '/video',
        policy: '/video/policy',
        list: '/video/list',
        play: '/video/play',
        transcodingStatus: '/video/transcodingStatus',
        remove: '/video/remove'
    }

    self.currentPolicy = null

    /**
     * Gets user's list of videos and also sorts out whether anything
     * is currently transcoding
     */
    self.refreshVideoList = function() {
        $.get(self.endpoints.list)
            .then(function(response) {
                // sort our videos, local var here to not annoy
                // bound elements, creating any necessary polling
                // intervals
                var videos = {
                    transcoding: [],
                    transcoded: []
                }
                $.each(response.videos.videos, function(idx, video) {
                    videos[ video.transcoded ? 'transcoded' : 'transcoding' ].push( video )
                    self.manageTranscodingIntervalFor( video )
                })

                // now update bound items
                self.videos = response.videos.videos
                self.transcodedVideos = videos.transcoded
                self.transcodingVideos = videos.transcoding

                // turn button off if we're at the limit
                var limitReached = "You're at your limit!"
                if ( self.maxVideos <= self.videos.length ) {
                    $('#upload-btn').prop('disabled', true)
                    $('#upload-btn').find('span').text(limitReached)
                } else if ( $('#upload-btn').find('span').text() == limitReached ) {
                    $('#upload-btn').prop('disabled', false)
                    $('#upload-btn').find('span').text("Upload A Video")
                }
            })
    }

    /**
     * For a given video dto, manages whether or not there should currently be a polling
     * request to check its transcoding status. Is black box: cleans up after self.
     * @param video
     */
    self.manageTranscodingIntervalFor = function (video) {
        if ( !self.transcodingIntervals[ video.id ] && !video.transcoded ) {
            self.transcodingIntervals[ video.id ] = setInterval( function() {

                $.get(self.endpoints.transcodingStatus + '/' + video.id)
                    .then(function(response) {
                        if ( response.status.transcoded ) {
                            // refresh
                            self.refreshVideoList()
                            // clean up
                            clearInterval(self.transcodingIntervals[ video.id ])
                        }
                    })
                    .fail(function(er) {
                        // quietly fail
                        console.error(er)
                    })
            }, self.transcodingPollInterval)
        }
    }

    /**
     * Gets a fresh signed upload policy.
     */
    self.updateCurrentPolicy = function () {
        // we'll re-create the uploader with updated policy
        if ( self.uploader ) {
            try {
                self.uploader.destroy()
            } catch (e) {
                // pluploader has an issue...
            }
        }

        $.get(self.endpoints.policy)
            .then(function(response) {
                self.currentPolicy = response
                self.configureUpload()
                self.updateUploadButtonState("Upload A Video", true)
            })
            .fail(function() {
                kickstart.bootstrap.alert("Something went wrong!", "Oops!")
                self.updateUploadButtonState("We're having problems", false)
            })

    }

    /**
     * Configures the plupload with the current S3 policy, attaching it to the
     * upload button
     */
    self.configureUpload = function() {
        var multipartParams = {
            'key': self.currentPolicy.key, // use filename as a key
            'Filename': self.currentPolicy.key, // adding this to keep consistency across the runtimes
            'AWSAccessKeyId' : self.currentPolicy.accessKey,
            'policy': self.currentPolicy.uploadPolicy.policy,
            'signature': self.currentPolicy.uploadPolicy.signature,
            'success_action_status': '201'
        }

        var uploader = new plupload.Uploader({
            browse_button: 'upload-btn',
            runtimes : 'html5',
            url : 'http://' + self.currentPolicy.bucket + '.s3.amazonaws.com/',

            multipart: true,

            multipart_params: multipartParams,

            filters : {
                // Maximum file size
                max_file_size : '10mb',
                // Specify what files to browse for
                mime_types: [
                    {title : "MP4 Movie files", extensions : "mp4"}
                ]
            },
        })

        uploader.init()

        uploader.bind('FilesAdded', function(up, files) {
            uploader.start();
            self.updateUploadButtonState("Uploading: 0%", false)

        });

        uploader.bind('UploadProgress', function(up, file) {
            self.updateUploadButtonState("Uploading: " + file.percent + "%", false)
        });

        uploader.bind('Error', function(up, err) {
            console.log("Error", err)
            kickstart.bootstrap.alert("We're having a problem with uploads right now, sorry!", "Uh-oh!")
            self.updateUploadButtonState("Working...", false)
            self.updateCurrentPolicy()

        });

        uploader.bind('UploadComplete', function() {
            self.createVideoFromCurrentPolicy()
        })

        self.uploader = uploader

    }

    /**
     * When a video is done uploading, uses current policy as key to request the API
     * create a record of the S3 object and start a transcoding process
     */
    self.createVideoFromCurrentPolicy = function() {
        self.updateUploadButtonState("Finishing...", false)
        $.get(self.endpoints.video + '?id=' + self.currentPolicy.id)
            .then(function(response) {
                if ( response.error ) {
                    kickstart.bootstrap.alert(response.error, "Uh-oh!")
                }
            })
            .fail(function() {
                kickstart.bootstrap.alert("We're sorry, there was an error uploading your video.", "Uh-oh!")

            })
            .always(function() {
                self.updateCurrentPolicy()
                self.refreshVideoList()
            })
    }

    self.removeVideo = function(id) {
        kickstart.bootstrap.confirm("Are you sure you want to remove this video?", "Are you sure?", "Yes", "No")
            .then(function(confirmed) {
                if ( confirmed ) {
                    $.get(self.endpoints.remove + '/' + id)
                        .fail(function() {
                            kickstart.bootstrap.alert("We had a problem removing your video.", "Uh-oh!")
                        })
                        .always(function() {
                            self.refreshVideoList()
                        })
                }
            })
    }

    /**
     * Can this device handle the single-format transcoding we're supporting?
     */
    self.checkPlaybackSupport = function() {
        var vid = document.createElement('video')
        self.playbackSupported = vid.canPlayType('application/vnd.apple.mpegurl') === "probably" || vid.canPlayType('application/vnd.apple.mpegurl') === "maybe"
    }


    /**
     * UI helper for the upload button
     *
     * @param text
     * @param enabled
     */
    self.updateUploadButtonState = function(text, enabled) {
        $('#upload-btn').prop('disabled', !enabled)
        $('#upload-btn').find('span').text(text)
    }

    // do immediately
    self.updateCurrentPolicy()
    self.refreshVideoList()
    self.checkPlaybackSupport()

    // set up simple Vue: just for rendering, not going full-bore
    new Vue({el: '#video-main', data: self})

    // events
    $('#video-card-holder').click(function(e) {
        var target = $(e.target)

        if ( target.hasClass('video-play-btn') ) {
            window.location = self.endpoints.play + '/' + target.data('video-id')
        }

        if ( target.hasClass('video-remove-btn') ) {
            self.removeVideo(target.data('video-id'))
        }

    })
}

$(function() {
    new VideoController(serverSideConfig)
})



