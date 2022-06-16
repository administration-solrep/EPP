function uploadFiles(files, loadingButton, successCallback, failureCallback) {
    if (files && files.length > 0) {
        _performUploadFiles(files, loadingButton, successCallback, failureCallback);
    } else {
        successCallback();
    }
}

function _performUploadFiles(files, loadingButton, successCallback, failureCallback) {
    let ajaxCallPath = $("#ajaxCallPath").val();
    let uploadBatchIdRequest = {
        method: "POST",
        dataType: "html",
        ajaxUrl: `${ajaxCallPath}/upload`,
        isChangeURL: false,
        caller: this,
        loadingButton: loadingButton,
    };

    callAjaxRequest(
        uploadBatchIdRequest,
        (_, batchIdJson) => {
            let batchId = JSON.parse(batchIdJson).batchId;
            _recursiveUploadFiles(files, batchId, 0, loadingButton, successCallback, failureCallback);
        },
        failureCallback
    );
}

function _recursiveUploadFiles(files, batchId, index, loadingButton, successCallback, failureCallback) {
    callAjaxRequest(
        _newUploadFileRequest(files[index], batchId, index, loadingButton),
        (_, statusJson) => {
            let uploaded = JSON.parse(statusJson).uploaded === "true";
            if (uploaded) {
                index += 1;
                if (index < files.length) {
                    _recursiveUploadFiles(files, batchId, index, loadingButton, successCallback, failureCallback);
                } else {
                    successCallback(batchId);
                }
            } else {
                failureCallback();
            }
        },
        failureCallback
    );
}

function _newUploadFileRequest(file, batchId, index, loadingButton) {
    let ajaxCallPath = $("#ajaxCallPath").val();
    return {
        method: "POST",
        dataType: "html",
        enctype: "multipart/form-data",
        ajaxUrl: `${ajaxCallPath}/upload/${batchId}/${index}`,
        isChangeURL: false,
        headers: {
            "X-File-Name": file.name,
            "X-File-Type": file.type,
        },
        dataToSend: file,
        loadingButton: loadingButton,
    };
}
