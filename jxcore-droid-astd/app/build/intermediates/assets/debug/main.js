// License information is available from LICENSE file

// main jxcore app file

var app;
global.webview = {};

webview.makeFunction = function(call_id) {
  var _this = this;
  _this.call_id = call_id;

  return function() {
    var arr = [];
    for (var i = 0; i < arguments.length; i++)
      arr[i] = arguments[i];

    var ret = {
      i: _this.call_id,
      a: arr
    };
    process.natives.asyncCallback(JSON.stringify(ret));
  }
}

webview.call = function(obj) {
  var method_name = obj[0];
  if (obj.length > 1) {
    obj = obj.slice(1);
  } else {
    obj = [];
  }

  for (var i = 0; i < obj.length; i++) {
    if (obj[i].jxcore_webview_callbackId) {
      var id = obj[i].jxcore_webview_callbackId;
      obj[i] = new webview.makeFunction(id);
    }
  }

  return app[method_name].apply(null, obj);
}

process.registerAssets = function() {
  var fs = require('fs');
  var folders = process.natives.assetReadDirSync();
  var root = process.cwd();
  var jxcore_root;

  var prepVirtualDirs = function() {
    var _ = {};
    for ( var o in folders) {
      var sp = o.split('/');
      var last = _;
      for ( var i in sp) {
        var loc = sp[i];
        if (!last[loc]) last[loc] = {};
        last = last[loc];
      }
      last['!s'] = folders[o];
    }

    folders = {};
    var sp = root.split('/');
    if (sp[0] === '') sp.shift();
    jxcore_root = folders;
    for ( var o in sp) {
      jxcore_root[sp[o]] = {};
      jxcore_root = jxcore_root[sp[o]];
    }

    jxcore_root['jxcore'] = _; // assets/jxcore -> /
  };

  prepVirtualDirs();

  var findIn = function(what, where) {
    var last = where;
    for ( var o in what) {
      var subject = what[o];
      if (!last[subject]) return;

      last = last[subject];
    }

    return last;
  }

  var readfilesync = function(pathname) {
    var n = pathname.indexOf(root);
    if (n === 0) {
      var location = pathname.replace(root + "/", "");
      return process.natives.assetReadSync(location);
    }
  };

  var getLast = function(location) {
    while (location[0] == '/')
      location = location.substr(1);
    while (location[location.length - 1] == '/')
      location = location.substr(0, location.length - 1);

    var dirs = location.split('/');

    var res = findIn(dirs, folders);
    if (!res) res = findIn(dirs, jxcore_root);

    return res;
  };

  var existssync = function(pathname) {
    var n = pathname.indexOf(root);
    if (n === 0 || n === -1) {
      var last = getLast(pathname);
      if (!last) return false;

      var result;
      if (typeof last['!s'] === 'undefined')
        result = {
          size: 0
        };
      else
        result = {
          size: last['!s']
        };

      return result;
    }
  };

  var readdirsync = function(pathname) {
    var n = pathname.indexOf(root);
    if (n === 0 || n === -1) {
      var last = getLast(pathname);
      if (!last || typeof last['!s'] !== 'undefined') return null;

      var arr = [];
      for ( var o in last) {
        var item = last[o];
        if (item && o != '!s') arr.push(o);
      }
      return arr;
    }
    return null;
  }

  var extension = {
    readFileSync: readfilesync,
    readDirSync: readdirsync,
    existsSync: existssync,
  };

  fs.setExtension("jxcore-java", extension);
}

process.registerAssets();

// do not load app before defining the fs extension
app = require('jxcore/app.js');
