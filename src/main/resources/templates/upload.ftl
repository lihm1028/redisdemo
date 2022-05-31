<html xmlns:th="https://www.thymeleaf.org">
<body>
<title> 上传文件 freemarker</title>
<div>
    <form method="POST" enctype="multipart/form-data" action="/">
        <table>
            <tr>
                <td>File to upload:</td>
                <td><input type="file" name="file"/></td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" value="Upload"/></td>
            </tr>
        </table>
    </form>
</div>

<div>
    <ul>

        <#list files as file>
            <li>
                <a href="${file}" >${file}</a>
            </li>
        </#list>

    </ul>
</div>

</body>
</html>