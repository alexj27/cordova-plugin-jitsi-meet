var exec = require('cordova/exec');

exports.loadURL = function(host, room, jwt, success, error) {
    exec(success, error, "JitsiPlugin", "loadURL", [host, room, jwt]);
};

exports.destroy = function(success, error) {
    exec(success, error, "JitsiPlugin", "destroy", []);
};

exports.saveJitsiSettings = function(json, success, error) {
    exec(success, error, "JitsiPlugin", "saveSettings", [JSON.stringify(json)]);
};
