function initPasteDragImg(Editor) {
    var doc = document.getElementById(Editor.id)
    doc.addEventListener('paste', function (event) {
        var items = (event.clipboardData || window.clipboardData).items;
        var file = null;
        if (items && items.length) {
            // 搜索剪切板items
            for (var i = 0; i < items.length; i++) {
                if (items[i].type.indexOf('image') !== -1 || items[i].type.indexOf('file') !== -1) {
                    file = items[i].getAsFile();
                    event.stopPropagation();
                    event.preventDefault();
                    break;
                }
            }
        } else {
            // console.log("Browser is not supported");
            return;
        }
        if (!file) {
            // console.log("Please paste an image file to upload");
            return;
        }
        uploadImg(file, Editor);
    });
    var dashboard = document.getElementById(Editor.id)
    dashboard.addEventListener("dragover", function (e) {
        e.preventDefault()
        e.stopPropagation()
    })
    dashboard.addEventListener("dragenter", function (e) {
        e.preventDefault()
        e.stopPropagation()
    })
    dashboard.addEventListener("drop", function (e) {
        e.preventDefault()
        e.stopPropagation()
        const files = this.files || e.dataTransfer.files;
        uploadImg(files[0], Editor);
    })
}

function uploadImg(file, Editor) {
    var formData = new FormData();
    var fileName = new Date().getTime() + "." + file.name.split(".").pop();

    var onSuccess = function (msg) {
        var success = msg['success'];
        if (success === true) {
            let name = msg["displayName"];
            let isImage = msg["isImage"];
            if (isImage === true) {
                Editor.insertValue("![" + name + "](" + msg["url"] + ")");
            } else {
                Editor.insertValue("[" + name + "](" + msg["url"] + ")");
            }
        } else {
            console.log(msg);
            alert("Failed to upload image: " + success);
        }
    };
    Editor.settings.imageUploadFunction(file, onSuccess);
    // formData.append('editormd-image-file', file, fileName);
    // $.ajax({
    //     url: Editor.settings.imageUploadURL,
    //     type: 'post',
    //     data: formData,
    //     processData: false,
    //     contentType: false,
    //     dataType: 'json',
    //     success:     });
}