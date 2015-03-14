// License information is available from LICENSE file

// this is the file works under jxcore
var fs = require('fs');
var path = require('path');

console.log("Hello To LogCat!", fs.readdirSync('jxcore/node_modules'));

// requiring a node module
var jsnice = require('json-nice');

var obj = {
  a: 1,
  b: 2
};
console.error("from node module:", jsnice(obj));

exports.concat = function(a, b, cb) {
  cb(a + b);
};