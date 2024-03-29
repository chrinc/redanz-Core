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
             src="${headerLink}"
        >
    </div>
</div>
<div style="width:100%;background:#ececec;text-align:left;font-family: Arial,serif;">
    <div style="padding:15px;color:#0b0c0c">
        <h3>Hi ${firstName}</h3>
        <p>
            ${base}
        </p>
        <blockquote style="Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px">
            <p style="Margin:0 0 20px 0;font-size:16px;line-height:25px;color:#0b0c0c">
                <a href=${link}>${reset_now}</a>
            </p>
        </blockquote>
        </br>
        ${expires}.
        <p>${regards} <br>
        ${team}
        </p>
    </div>
</div>
</body>
</html>
