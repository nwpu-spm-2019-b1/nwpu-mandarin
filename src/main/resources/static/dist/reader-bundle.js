(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else {
		var a = factory();
		for(var i in a) (typeof exports === 'object' ? exports : root)[i] = a[i];
	}
})(window, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "/static/dist/";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./src/main/resources/ui/js/reader.js");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./node_modules/decode-uri-component/index.js":
/*!****************************************************!*\
  !*** ./node_modules/decode-uri-component/index.js ***!
  \****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nfunction _typeof(obj) { if (typeof Symbol === \"function\" && typeof Symbol.iterator === \"symbol\") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === \"function\" && obj.constructor === Symbol && obj !== Symbol.prototype ? \"symbol\" : typeof obj; }; } return _typeof(obj); }\n\nvar token = '%[a-f0-9]{2}';\nvar singleMatcher = new RegExp(token, 'gi');\nvar multiMatcher = new RegExp('(' + token + ')+', 'gi');\n\nfunction decodeComponents(components, split) {\n  try {\n    // Try to decode the entire string first\n    return decodeURIComponent(components.join(''));\n  } catch (err) {// Do nothing\n  }\n\n  if (components.length === 1) {\n    return components;\n  }\n\n  split = split || 1; // Split the array in 2 parts\n\n  var left = components.slice(0, split);\n  var right = components.slice(split);\n  return Array.prototype.concat.call([], decodeComponents(left), decodeComponents(right));\n}\n\nfunction decode(input) {\n  try {\n    return decodeURIComponent(input);\n  } catch (err) {\n    var tokens = input.match(singleMatcher);\n\n    for (var i = 1; i < tokens.length; i++) {\n      input = decodeComponents(tokens, i).join('');\n      tokens = input.match(singleMatcher);\n    }\n\n    return input;\n  }\n}\n\nfunction customDecodeURIComponent(input) {\n  // Keep track of all the replacements and prefill the map with the `BOM`\n  var replaceMap = {\n    '%FE%FF': \"\\uFFFD\\uFFFD\",\n    '%FF%FE': \"\\uFFFD\\uFFFD\"\n  };\n  var match = multiMatcher.exec(input);\n\n  while (match) {\n    try {\n      // Decode as big chunks as possible\n      replaceMap[match[0]] = decodeURIComponent(match[0]);\n    } catch (err) {\n      var result = decode(match[0]);\n\n      if (result !== match[0]) {\n        replaceMap[match[0]] = result;\n      }\n    }\n\n    match = multiMatcher.exec(input);\n  } // Add `%C2` at the end of the map to make sure it does not replace the combinator before everything else\n\n\n  replaceMap['%C2'] = \"\\uFFFD\";\n  var entries = Object.keys(replaceMap);\n\n  for (var i = 0; i < entries.length; i++) {\n    // Replace all decoded components\n    var key = entries[i];\n    input = input.replace(new RegExp(key, 'g'), replaceMap[key]);\n  }\n\n  return input;\n}\n\nmodule.exports = function (encodedURI) {\n  if (typeof encodedURI !== 'string') {\n    throw new TypeError('Expected `encodedURI` to be of type `string`, got `' + _typeof(encodedURI) + '`');\n  }\n\n  try {\n    encodedURI = encodedURI.replace(/\\+/g, ' '); // Try the built in decoder first\n\n    return decodeURIComponent(encodedURI);\n  } catch (err) {\n    // Fallback to a more advanced decoder\n    return customDecodeURIComponent(encodedURI);\n  }\n};\n\n//# sourceURL=webpack:///./node_modules/decode-uri-component/index.js?");

/***/ }),

/***/ "./node_modules/query-string/index.js":
/*!********************************************!*\
  !*** ./node_modules/query-string/index.js ***!
  \********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nfunction _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }\n\nfunction _nonIterableRest() { throw new TypeError(\"Invalid attempt to destructure non-iterable instance\"); }\n\nfunction _iterableToArrayLimit(arr, i) { if (!(Symbol.iterator in Object(arr) || Object.prototype.toString.call(arr) === \"[object Arguments]\")) { return; } var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i[\"return\"] != null) _i[\"return\"](); } finally { if (_d) throw _e; } } return _arr; }\n\nfunction _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }\n\nfunction _typeof(obj) { if (typeof Symbol === \"function\" && typeof Symbol.iterator === \"symbol\") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === \"function\" && obj.constructor === Symbol && obj !== Symbol.prototype ? \"symbol\" : typeof obj; }; } return _typeof(obj); }\n\nfunction _toConsumableArray(arr) { return _arrayWithoutHoles(arr) || _iterableToArray(arr) || _nonIterableSpread(); }\n\nfunction _nonIterableSpread() { throw new TypeError(\"Invalid attempt to spread non-iterable instance\"); }\n\nfunction _iterableToArray(iter) { if (Symbol.iterator in Object(iter) || Object.prototype.toString.call(iter) === \"[object Arguments]\") return Array.from(iter); }\n\nfunction _arrayWithoutHoles(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = new Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } }\n\nvar strictUriEncode = __webpack_require__(/*! strict-uri-encode */ \"./node_modules/strict-uri-encode/index.js\");\n\nvar decodeComponent = __webpack_require__(/*! decode-uri-component */ \"./node_modules/decode-uri-component/index.js\");\n\nvar splitOnFirst = __webpack_require__(/*! split-on-first */ \"./node_modules/split-on-first/index.js\");\n\nfunction encoderForArrayFormat(options) {\n  switch (options.arrayFormat) {\n    case 'index':\n      return function (key) {\n        return function (result, value) {\n          var index = result.length;\n\n          if (value === undefined) {\n            return result;\n          }\n\n          if (value === null) {\n            return [].concat(_toConsumableArray(result), [[encode(key, options), '[', index, ']'].join('')]);\n          }\n\n          return [].concat(_toConsumableArray(result), [[encode(key, options), '[', encode(index, options), ']=', encode(value, options)].join('')]);\n        };\n      };\n\n    case 'bracket':\n      return function (key) {\n        return function (result, value) {\n          if (value === undefined) {\n            return result;\n          }\n\n          if (value === null) {\n            return [].concat(_toConsumableArray(result), [[encode(key, options), '[]'].join('')]);\n          }\n\n          return [].concat(_toConsumableArray(result), [[encode(key, options), '[]=', encode(value, options)].join('')]);\n        };\n      };\n\n    case 'comma':\n      return function (key) {\n        return function (result, value, index) {\n          if (value === null || value === undefined || value.length === 0) {\n            return result;\n          }\n\n          if (index === 0) {\n            return [[encode(key, options), '=', encode(value, options)].join('')];\n          }\n\n          return [[result, encode(value, options)].join(',')];\n        };\n      };\n\n    default:\n      return function (key) {\n        return function (result, value) {\n          if (value === undefined) {\n            return result;\n          }\n\n          if (value === null) {\n            return [].concat(_toConsumableArray(result), [encode(key, options)]);\n          }\n\n          return [].concat(_toConsumableArray(result), [[encode(key, options), '=', encode(value, options)].join('')]);\n        };\n      };\n  }\n}\n\nfunction parserForArrayFormat(options) {\n  var result;\n\n  switch (options.arrayFormat) {\n    case 'index':\n      return function (key, value, accumulator) {\n        result = /\\[(\\d*)\\]$/.exec(key);\n        key = key.replace(/\\[\\d*\\]$/, '');\n\n        if (!result) {\n          accumulator[key] = value;\n          return;\n        }\n\n        if (accumulator[key] === undefined) {\n          accumulator[key] = {};\n        }\n\n        accumulator[key][result[1]] = value;\n      };\n\n    case 'bracket':\n      return function (key, value, accumulator) {\n        result = /(\\[\\])$/.exec(key);\n        key = key.replace(/\\[\\]$/, '');\n\n        if (!result) {\n          accumulator[key] = value;\n          return;\n        }\n\n        if (accumulator[key] === undefined) {\n          accumulator[key] = [value];\n          return;\n        }\n\n        accumulator[key] = [].concat(accumulator[key], value);\n      };\n\n    case 'comma':\n      return function (key, value, accumulator) {\n        var isArray = typeof value === 'string' && value.split('').indexOf(',') > -1;\n        var newValue = isArray ? value.split(',') : value;\n        accumulator[key] = newValue;\n      };\n\n    default:\n      return function (key, value, accumulator) {\n        if (accumulator[key] === undefined) {\n          accumulator[key] = value;\n          return;\n        }\n\n        accumulator[key] = [].concat(accumulator[key], value);\n      };\n  }\n}\n\nfunction encode(value, options) {\n  if (options.encode) {\n    return options.strict ? strictUriEncode(value) : encodeURIComponent(value);\n  }\n\n  return value;\n}\n\nfunction decode(value, options) {\n  if (options.decode) {\n    return decodeComponent(value);\n  }\n\n  return value;\n}\n\nfunction keysSorter(input) {\n  if (Array.isArray(input)) {\n    return input.sort();\n  }\n\n  if (_typeof(input) === 'object') {\n    return keysSorter(Object.keys(input)).sort(function (a, b) {\n      return Number(a) - Number(b);\n    }).map(function (key) {\n      return input[key];\n    });\n  }\n\n  return input;\n}\n\nfunction removeHash(input) {\n  var hashStart = input.indexOf('#');\n\n  if (hashStart !== -1) {\n    input = input.slice(0, hashStart);\n  }\n\n  return input;\n}\n\nfunction extract(input) {\n  input = removeHash(input);\n  var queryStart = input.indexOf('?');\n\n  if (queryStart === -1) {\n    return '';\n  }\n\n  return input.slice(queryStart + 1);\n}\n\nfunction parseValue(value, options) {\n  if (options.parseNumbers && !Number.isNaN(Number(value)) && typeof value === 'string' && value.trim() !== '') {\n    value = Number(value);\n  } else if (options.parseBooleans && value !== null && (value.toLowerCase() === 'true' || value.toLowerCase() === 'false')) {\n    value = value.toLowerCase() === 'true';\n  }\n\n  return value;\n}\n\nfunction parse(input, options) {\n  options = Object.assign({\n    decode: true,\n    sort: true,\n    arrayFormat: 'none',\n    parseNumbers: false,\n    parseBooleans: false\n  }, options);\n  var formatter = parserForArrayFormat(options); // Create an object with no prototype\n\n  var ret = Object.create(null);\n\n  if (typeof input !== 'string') {\n    return ret;\n  }\n\n  input = input.trim().replace(/^[?#&]/, '');\n\n  if (!input) {\n    return ret;\n  }\n\n  var _iteratorNormalCompletion = true;\n  var _didIteratorError = false;\n  var _iteratorError = undefined;\n\n  try {\n    for (var _iterator = input.split('&')[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {\n      var param = _step.value;\n\n      var _splitOnFirst = splitOnFirst(param.replace(/\\+/g, ' '), '='),\n          _splitOnFirst2 = _slicedToArray(_splitOnFirst, 2),\n          key = _splitOnFirst2[0],\n          value = _splitOnFirst2[1]; // Missing `=` should be `null`:\n      // http://w3.org/TR/2012/WD-url-20120524/#collect-url-parameters\n\n\n      value = value === undefined ? null : decode(value, options);\n      formatter(decode(key, options), value, ret);\n    }\n  } catch (err) {\n    _didIteratorError = true;\n    _iteratorError = err;\n  } finally {\n    try {\n      if (!_iteratorNormalCompletion && _iterator[\"return\"] != null) {\n        _iterator[\"return\"]();\n      }\n    } finally {\n      if (_didIteratorError) {\n        throw _iteratorError;\n      }\n    }\n  }\n\n  for (var _i = 0, _Object$keys = Object.keys(ret); _i < _Object$keys.length; _i++) {\n    var key = _Object$keys[_i];\n    var value = ret[key];\n\n    if (_typeof(value) === 'object' && value !== null) {\n      for (var _i2 = 0, _Object$keys2 = Object.keys(value); _i2 < _Object$keys2.length; _i2++) {\n        var k = _Object$keys2[_i2];\n        value[k] = parseValue(value[k], options);\n      }\n    } else {\n      ret[key] = parseValue(value, options);\n    }\n  }\n\n  if (options.sort === false) {\n    return ret;\n  }\n\n  return (options.sort === true ? Object.keys(ret).sort() : Object.keys(ret).sort(options.sort)).reduce(function (result, key) {\n    var value = ret[key];\n\n    if (Boolean(value) && _typeof(value) === 'object' && !Array.isArray(value)) {\n      // Sort object keys, not values\n      result[key] = keysSorter(value);\n    } else {\n      result[key] = value;\n    }\n\n    return result;\n  }, Object.create(null));\n}\n\nexports.extract = extract;\nexports.parse = parse;\n\nexports.stringify = function (object, options) {\n  if (!object) {\n    return '';\n  }\n\n  options = Object.assign({\n    encode: true,\n    strict: true,\n    arrayFormat: 'none'\n  }, options);\n  var formatter = encoderForArrayFormat(options);\n  var keys = Object.keys(object);\n\n  if (options.sort !== false) {\n    keys.sort(options.sort);\n  }\n\n  return keys.map(function (key) {\n    var value = object[key];\n\n    if (value === undefined) {\n      return '';\n    }\n\n    if (value === null) {\n      return encode(key, options);\n    }\n\n    if (Array.isArray(value)) {\n      return value.reduce(formatter(key), []).join('&');\n    }\n\n    return encode(key, options) + '=' + encode(value, options);\n  }).filter(function (x) {\n    return x.length > 0;\n  }).join('&');\n};\n\nexports.parseUrl = function (input, options) {\n  return {\n    url: removeHash(input).split('?')[0] || '',\n    query: parse(extract(input), options)\n  };\n};\n\n//# sourceURL=webpack:///./node_modules/query-string/index.js?");

/***/ }),

/***/ "./node_modules/split-on-first/index.js":
/*!**********************************************!*\
  !*** ./node_modules/split-on-first/index.js ***!
  \**********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nmodule.exports = function (string, separator) {\n  if (!(typeof string === 'string' && typeof separator === 'string')) {\n    throw new TypeError('Expected the arguments to be of type `string`');\n  }\n\n  if (separator === '') {\n    return [string];\n  }\n\n  var separatorIndex = string.indexOf(separator);\n\n  if (separatorIndex === -1) {\n    return [string];\n  }\n\n  return [string.slice(0, separatorIndex), string.slice(separatorIndex + separator.length)];\n};\n\n//# sourceURL=webpack:///./node_modules/split-on-first/index.js?");

/***/ }),

/***/ "./node_modules/strict-uri-encode/index.js":
/*!*************************************************!*\
  !*** ./node_modules/strict-uri-encode/index.js ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nmodule.exports = function (str) {\n  return encodeURIComponent(str).replace(/[!'()*]/g, function (x) {\n    return \"%\".concat(x.charCodeAt(0).toString(16).toUpperCase());\n  });\n};\n\n//# sourceURL=webpack:///./node_modules/strict-uri-encode/index.js?");

/***/ }),

/***/ "./src/main/resources/ui/js/reader.js":
/*!********************************************!*\
  !*** ./src/main/resources/ui/js/reader.js ***!
  \********************************************/
/*! exports provided: queryString */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony import */ var query_string__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! query-string */ \"./node_modules/query-string/index.js\");\n/* harmony import */ var query_string__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(query_string__WEBPACK_IMPORTED_MODULE_0__);\n/* harmony reexport (default from non-harmony) */ __webpack_require__.d(__webpack_exports__, \"queryString\", function() { return query_string__WEBPACK_IMPORTED_MODULE_0___default.a; });\n\n\n\n//# sourceURL=webpack:///./src/main/resources/ui/js/reader.js?");

/***/ })

/******/ });
});