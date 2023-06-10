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
             src="https://stirit.ch/wp-content/uploads/assets/web_header_01.png"
        >
    </div>
</div>
<div style="width:100%;background:#ececec;text-align:left;font-family: Arial,serif;">
    <div style="padding:15px;">
        <h3>Hi ${firstName}</h3>
        ${content}
        ${regards} <br>
        ${team}
    </div>
</div>
</body>
</html>
