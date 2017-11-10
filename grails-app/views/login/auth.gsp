
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="layout" content="main">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Please sign in.</title>
</head>

<body>

<main role="main" class="container-fluid">
    <form action="${postUrl ?: '/login/authenticate'}" method="POST" id="loginForm" autocomplete="off">
    <div class="row">
        <div class="col-xs-12 col-sm-12 col-md-6 offset-md-3">
            <h1>Please sign in.</h1>
            <div class="rule"></div>
            <g:if test='${flash.message}'>
                <bp:alert intent="danger">${flash.message}</bp:alert>
            </g:if>

            <bp:field label="Username">
                <input type="text" class="form-control" name="${usernameParameter ?: 'username'}" id="username" autofocus />
            </bp:field>
            <bp:field label="Password" help="Provided to you via e-mail.">
                <input type="password" class="form-control" name="${passwordParameter ?: 'password'}" id="password" />
            </bp:field>
            <div class="offset-md-3 col-md-9 offset-form-control">
                <button type="submit" class="btn btn-primary">Let's go!</button>
            </div>
        </div>
    </div>
    </form>
</main>

</body>
</html>
