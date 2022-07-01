<!DOCTYPE html>
<html>
<head>
    <base target="_top">
    <?!= include("css.html") ?>
</head>
<body>
<div style="width:730px;background:#ececec;text-align:left;font-family: Arial;">
    <img src="https://stirit.ch/wp-content/uploads/2022/06/web_header_01.png" style="margin:15px" width="700"><br/>
    <div style="padding:15px;">
        <h3>Hi ${firstName}</h3>
        <p>
            ${header}
        </p>
        <p>
            ${base01} </br>
            ${base02}
        </p>
        <p>
            ${base03}
        </p>
        <p>
            ${details} <a href=${link}>${account}.</a>
        </p>
        <p>${see_you}</p>
        ${regards} </br>
        ${team}
    </div>
</div>
</body>
</html>
