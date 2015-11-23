$(function () {

    var searchToHash = function () {
        var h = {};
        if (window.location.search == undefined || window.location.search.length < 1) {
            return h;
        }
        var q = window.location.search.slice(1).split('&');
        for (var i = 0; i < q.length; i++) {
            var keyVal = q[i].split('=');
            // replace '+' (alt space) char explicitly since decode does not
            var hkey = decodeURIComponent(keyVal[0]).replace(/\+/g, ' ');
            var hval = decodeURIComponent(keyVal[1]).replace(/\+/g, ' ');
            if (h[hkey] == undefined) {
                h[hkey] = [];
            }
            h[hkey].push(hval);
        }
        return h;
    };


    var hashToSearch = function (h) {
        var search = "?";
        for (var k in h) {
            for (var i = 0; i < h[k].length; i++) {
                search += search == "?" ? "" : "&";
                search += encodeURIComponent(k) + "=" + encodeURIComponent(h[k][i]);
            }
        }
        return search;
    };

    var replaceQueryParameter = function (name, value) {
        var newSearchHash = searchToHash();
        delete newSearchHash[name];
        newSearchHash[decodeURIComponent(name)] = [decodeURIComponent(value)];
        return hashToSearch(newSearchHash);
    };

    var handleChooseLocale = function (lang) {
        return function (e) {
            e.preventDefault();
            e.stopPropagation();
            var target = $(e.target);
            if (target.hasClass("selected")) {
                lang = (lang === "en" ? "nl" : "en");
            }
            var newSearch = replaceQueryParameter("lang", lang);
            window.location.search = newSearch;
        };
    };

    $("#header_lang_en").click(handleChooseLocale("en"));
    $("#header_lang_nl").click(handleChooseLocale("nl"));


});
