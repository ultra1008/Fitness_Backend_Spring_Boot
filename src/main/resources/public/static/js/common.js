function getRequestParam(name){
    if (name = (new RegExp('[?&amp;]' + encodeURIComponent(name) + '=([^&amp;]*)')).exec(window.location.search)) {
        return decodeURIComponent(name[1]);
    }
}

$(function() {
    $('#logout').click(function() {
        document.cookie = 'Authorization=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
        window.location = '/login.html?logout=true';
    });
});
