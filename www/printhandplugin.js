// var exec = require('cordova/exec');

var PrintHandPlugin = {
    printWithHttpURL: function (callBackSuccess, callBackFail, webUrl) {
        cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "printWithHttpURL", [webUrl]);
    },
     printWebPageWithContain: function (callBackSuccess, callBackFail, htmlFormatString) {
         cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "printWebPageWithContain", [htmlFormatString]);
     },
    printWebPageWithContain: function (callBackSuccess, callBackFail, htmlFormatString) {
        cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "printWebPageWithContain", [htmlFormatString]);
    },
    printImage: function (callBackSuccess, callBackFail, imagePath, activeView) {
        /*Active View set true/false*/
        cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "printImage", [imagePath, activeView]);
    },
    printFile: function (callBackSuccess, callBackFail, filename, fileDescription, mimeType) {
        cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "printFile", [filename, fileDescription, mimeType]);
    },
    printFileWithoutUI: function (callBackSuccess, callBackFail, filename, fileDescription, mimeType) {
        cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "printFileWithoutUI", [filename, fileDescription, mimeType]);
    },
    setupPrinter: function (callBackSuccess, callBackFail) {
        cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "setupPrinter");
    },
    configPrinter: function (callBackSuccess, callBackFail) {
        cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "configPrinter");
    },
    startService: function (callBackSuccess, callBackFail) {
            cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "startService");
        },
    echo: function (callBackSuccess, callBackFail) {
        cordova.exec(callBackSuccess, callBackFail, "IntentApiPrintHandPlugin", "echo", ['test 1']);
    }
};
module.exports = PrintHandPlugin;

