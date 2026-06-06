



/**
 * 다음 주소 API
 */
function openPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('zipcode').value = data.zonecode;
            document.getElementById('address').value = data.address;
            document.getElementById('addressDetail').focus();
        }
    }).open();
}

window.openPostcode = openPostcode;