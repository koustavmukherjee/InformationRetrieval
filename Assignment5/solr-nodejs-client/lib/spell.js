const spell = require('spell');
const dict = spell();
const fs = require('fs');
let data = fs.readFileSync("dict/big.json", "utf8");
dict.load(JSON.parse(data));

let getCorrectedPhrase = function(words) {
    words = words.split(" ");
    let results = [];
    for (let i = 0; i < words.length; i++) {
        results[i] = dict.lucky(words[i]);
    }
    return results.join(' ');
};

module.exports = {
    getCorrectedPhrase: getCorrectedPhrase
};