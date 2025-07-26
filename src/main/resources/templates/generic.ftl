<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <?!= include("css.html") ?>
</head>
<body style="-webkit-text-size-adjust:none; -ms-text-size-adjust:none; font-size:16px; font-family: Arial, sans-serif;">

<div style="width:100%; height:221px; overflow:hidden; display:flex; justify-content:center; align-items:center;">
    <img src="${headerLink}/assets/graphics/email_header_01.png"
         alt="Header"
         style="height:221px; width:auto; min-width:100%; object-fit:cover;">
</div>
<div style="width:100%; background:#ececec; text-align:left; font-family: Arial, sans-serif; font-size:16px; line-height:22px; -webkit-text-size-adjust:none;">
    <div style="padding:15px;">

        <div style="text-align:center; padding-bottom:15px">
            <em style="font-size:16px; line-height:22px;">${changeLanguage}</em>
        </div>

        <h3 style="font-size:18px; line-height:24px; margin:0; font-family: Arial, sans-serif;">Hi ${firstName}</h3>

        <p style="font-size:16px; line-height:22px; font-family: Arial, sans-serif; margin:10px 0;">
            ${content}
        </p>

        <table style="width:100%; height:150px; font-size:16px; line-height:22px;">
            <tr>
                <!-- LEFT CELL -->
                <td style="width:50%; vertical-align:bottom; padding:0 0 10px 0; font-size:16px; line-height:22px; font-family: Arial, sans-serif;">
                    ${regards}<br>
                    ${team}<br/><br/>
                    *******************<br/>
                    <a href="mailto:info@stirit.ch" style="color:#0000ee; font-size:16px;">info@stirit.ch</a><br/>
                    <a href="http://www.stirit.ch" target="_blank" style="color:#0000ee; font-size:16px;">www.stirit.ch</a>

                    <div style="margin-top:8px;">
                        <a href="https://www.instagram.com/stiritfestival" target="_blank" style="margin-right:5px; display:inline-block;">
                            <img src="${headerLink}/assets/graphics/email_logo_insta_01.png" alt="Instagram"
                                 style="width:40px; height:40px;">
                        </a>
                        <a href="https://www.facebook.com/stirit" target="_blank" style="display:inline-block;">
                            <img src="${headerLink}/assets/graphics/email_logo_fb_01.png" alt="Facebook"
                                 style="width:40px; height:40px;">
                        </a>
                    </div>
                </td>

                <!-- RIGHT CELL -->
                <td style="width:50%; text-align:right; vertical-align:bottom; padding-right:20px;">
                    <a href="https://www.stirit.ch">
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
