// License information is available from LICENSE file

// this is the file works under jxcore
var fs = require('fs');
var path = require('path');

console.log("Hello To LogCat!", fs.readdirSync('jxcore/node_modules'));

// requiring a node module
var jsnice = require('json-nice');

// test jxm
var jxm = require('jxm');

if (jxm) {
  console.log('jxm test passed');
}

var obj = {
  a: 1,
  b: 2
};

console.log("from node module:", jsnice(obj));

exports.concat = function(a, b, cb) {
  jxcore.tasks.addTask(function(){
    var start = process.hrtime();
    var q = 0;
    for (var i=0; i<1e5; i++) {
      q += i%2;
      q %= 100;
    }
    return process.hrtime(start)[1] / 1000000;
  }, null, function(res) {
    cb(res);
  });
};