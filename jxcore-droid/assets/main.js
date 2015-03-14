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

var fs = require('fs');
var folders = process.natives.assetReadDirSync();
var root = process.cwd();

var readfilesync = function(pathname) {
  var n = pathname.indexOf(root);
  if (n === 0) {
    var location = pathname.replace(root + "/", "");
    return process.natives.assetReadSync(location);
  }
};

var getLast = function(pathname) {
  var location = pathname.replace(root + "/jxcore", "");
  var dirs = location.split('/');
  dirs[0] = '/';
  var last = folders;
  for ( var o in dirs) {
    last = last[dirs[o]];
    if (!last) { return null; }
  }
  return last;
};

var existssync = function(pathname) {
  var n = pathname.indexOf(root);
  if (n === 0 || n === -1) {
    var last = getLast(pathname);
    if (!last) return false;

    var result;
    if (last.f)
      result = {
        size: 0
      };
    else
      result = {
        size: last.s
      };

    return result;
  }
};

var readdirsync = function(pathname) {
  var n = pathname.indexOf(root);
  if (n === 0 || n === -1) {
    var last = getLast(pathname);
    if (!last || last.f === 0) return null;

    var arr = [];
    for ( var o in last) {
      var item = last[o];
      if (item && (item.f === 0 || item.f === 1)) arr.push(o);
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

// do not load app before defining the fs extension
app = require('jxcore/app.js');
