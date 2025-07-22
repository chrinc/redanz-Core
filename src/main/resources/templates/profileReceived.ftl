<!DOCTYPE html>
<html>
<head>
    <base target="_top">
    <?!= include("css.html") ?>
</head>
<body>

<div style="width:100%; height:221px; overflow:hidden; display:flex; justify-content:center; align-items:center;">
    <img src="${headerLink}/assets/graphics/email_header_01.png"
         alt="Header"
         style="height:221px; width:auto; min-width:100%; object-fit:cover;">
</div>
<div style="width:100%;background:#ececec;text-align:left;font-family: Arial,serif;">
    <div style="padding:15px;color:#0b0c0c">
        <div style="text-align: center"><em>${changeLanguage}</em></div>
        <h3>Hi ${firstName}</h3>
        <p>
            ${base}
        </p>
        <blockquote
                style="Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:18px;line-height:25px">
            <p style="Margin:0 0 20px 0;font-size:18px;line-height:25px;color:#0b0c0c">
                <a href=${registrationLink}>${activate_now}</a>
            </p>
        </blockquote>
        <br><br>
        <table style="width:100%; height:150px;"> <!-- height can be adjusted -->
            <tr>
                <!-- LEFT CELL -->
                <td style="width:50%; vertical-align:bottom; padding:0 0 10px 0;">
                    ${regards}<br>
                    ${team}<br/><br/>
                    *******************<br/>
                    <a href="mailto:info@stirit.ch" style="color:#0000ee;">info@stirit.ch</a><br/>
                    <a href="http://www.stirit.ch" target="_blank" style="color:#0000ee;">www.stirit.ch</a>
                    <div style="margin-top:8px;">
                        <a href="https://www.instagram.com/stiritfestival" target="_blank" style="margin-right:5px;display:inline-block;">
                            <img src="${headerLink}/assets/graphics/email_logo_insta_01.png" alt="Instagram"
                                 style="width:40px;height:40px;">
                        </a>
                        <a href="https://www.facebook.com/groups/stiritfestival" target="_blank" style="display:inline-block;">
                            <img src="${headerLink}/assets/graphics/email_logo_fb_01.png" alt="Facebook"
                                 style="width:40px;height:40px;">
                        </a>
                    </div>
                </td>

                <!-- RIGHT CELL -->
                <td style="width:50%; text-align:right; vertical-align:bottom; padding-right:20px;">
                    <a href="http://www.stirit.ch">
                        <img src="${headerLink}/assets/graphics/email_logo_round_01.png" alt="Stir it! Logo"
                             style="width:150px;height:auto;">
                    </a>
                </td>
            </tr>
        </table>
    </div>
</div>
</body>
</html>
