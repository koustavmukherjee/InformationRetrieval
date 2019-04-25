const Tokenizer = require('sentence-tokenizer');
const tokenizer = new Tokenizer('Snippet');
const fs = require('fs');
const FuzzySearch = require('fuzzy-search');
const Q = require('q');
var highlighter = require('keyword-highlighter');

const generateSnippet = function(filepath, id, term) {
    const filename = filepath + id + '.txt';
    let deferred = Q.defer();
    let data = fs.readFileSync(filename, 'utf8');
    let text = data.replace(/^\s*/gm, '').split('\n');
    let sentences = [];
    for(let i = 0; i < text.length; i++) {
        let line = text[i];
        tokenizer.setEntry(line);
        let tokens = tokenizer.getSentences();
        for(let j = 0; j < tokens.length; j++)
            sentences.push(tokens[j]);
    }
    const searcher = new FuzzySearch(text, {sort: true});
    const search_results = searcher.search(term);
    //const result = search_results[0];
    let result = '';
    for(let i = 0; i < search_results.length; i++) {
        result += search_results[i] + '. ';
        if(result.length > 160)
            break;
    }
    if(result)
        return highlighter(term, result);
    else
        return undefined;
};

const generateSnippetUsingRegex = function(filepath, id, term) {
    term = term.toLowerCase();
    const filename = filepath + id + '.txt';
    let data = fs.readFileSync(filename, 'utf8');
    let text = data.replace(/^\s*/gm, '').split('\n');

    let regex_all = "(?=.*\\b" + term.split(' ').join('\\b)(?=.*') + "\\b).+";
    regex_all = new RegExp(regex_all);

    let regex_any = "\\b(";
    regex_any += term.split(' ').join('|');
    regex_any += ")\\b";
    regex_any = new RegExp(regex_any,"ig");

    let sentences = [];
    for(let i = 0; i < text.length; i++) {
        let line = text[i];
        tokenizer.setEntry(line);
        let tokens = tokenizer.getSentences();
        for(let j = 0; j < tokens.length; j++)
            sentences.push(tokens[j]);
    }

    let result = '';

    for(let i = 0; i < sentences.length; i++) {
        let sentence = sentences[i].trim();
        if(result.length > 160)
            break;
        if(sentence.toLowerCase().includes(term) && (result.length + sentence.length <= 160)) {
            result += sentence[sentence.length - 1] == "." ? sentence + ' ' : sentence + '. ';
            sentences.splice(i, 1);
        }
    }

    for(let i = 0; i < sentences.length; i++) {
        let sentence = sentences[i].trim();
        if(result.length > 160)
            break;
        if(sentence.toLowerCase().match(regex_all) && (result.length + sentence.length <= 160)) {
            result += sentence[sentence.length - 1] == "." ? sentence + ' ' : sentence + '. ';
            sentences.splice(i, 1);
        }
    }

    for(let i = 0; i < sentences.length; i++) {
        let sentence = sentences[i].trim();
        if(result.length > 160)
            break;
        if(sentence.toLowerCase().match(regex_any) && (result.length + sentence.length <= 160)) {
            result += sentence[sentence.length - 1] == "." ? sentence + ' ' : sentence + '. ';
            sentences.splice(i, 1);
        }
    }

    if(result)
        return highlighter(term, result);
    else
        return 'NA';
};

module.exports = {
    generateSnippet: generateSnippet,
    generateSnippetUsingRegex: generateSnippetUsingRegex
};