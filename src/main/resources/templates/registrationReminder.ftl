<!DOCTYPE html>
<html>
<head>
    <base target="_top">
    <?!= include("css.html") ?>
</head>
<body>
<div style="background-color: #ececec;display: flex;width: 100%;">
    <div style="margin: auto;display: flex;width: 100%">
        <img style="margin: auto;max-width: 100%;"
             src="${headerLink}/assets/graphics/email_header_01.png"
        >
    </div>
</div>
<div style="width:100%;background:#ececec;text-align:left;font-family: Arial,serif;">
    <div style="padding:15px;">
        <h3>Hi ${firstName}</h3>
        <p>
            ${header}
        </p>
        <p>
            ${base01}
        </p>
        <p>
            ${base02}
        </p>
        <p>
            ${base03} ${details} <a href=${loginLink}>${account}.</a>
        </p>
        <p>${regards} <br>
            ${team}
        </p>
    </div>
</div>
</body>
</html>
