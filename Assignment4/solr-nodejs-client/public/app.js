'use strict';

// Declare app level module which depends on views, and core components
angular.module('myApp', [
]).
config(['$locationProvider', function($locationProvider) {
  $locationProvider.hashPrefix('!');
}]).
controller('main',function($scope, $http){
  $scope.search_query = undefined;
  $scope.lucene_search_results = {};
  $scope.page_rank_search_results = {};
  $scope.overlaps = 0;

  $scope.lucene_start = 0;
  $scope.lucene_rows = 10;
  $scope.lucene_search_term = undefined;
  $scope.total_lucene_results = 0;

  $scope.page_rank_start = 0;
  $scope.page_rank_rows = 10;
  $scope.page_rank_search_term = undefined;
  $scope.total_page_rank_results = 0;

  $scope.undefined = undefined;

  $scope.lucene_algo_desc = 'LUCENE BASED';
  $scope.page_rank_algo_desc = 'PAGE RANK BASED';

  let colors = ['#0000FF', '#00FF00', '#FFFF00', '#FF00FF',
                '#00FFFF', '#800000', '#FF0000', '#008080',
                '#808080', '#000080'];

  let assignStyle = function(obj1, obj2, color) {
      obj1.style = {"background-color": color};
      obj2.style = {"background-color": color};
      $scope.overlaps++;
  };

  $scope.clear = function() {
    $scope.search_query = undefined;
    $scope.overlaps = 0;
    $scope.lucene_search_results = {};
    $scope.page_rank_search_results = {};

    $scope.lucene_start = 0;
    $scope.lucene_rows = 10;
    $scope.lucene_search_term = undefined;
    $scope.total_lucene_results = 0;

    $scope.page_rank_start = 0;
    $scope.page_rank_rows = 10;
    $scope.page_rank_search_term = undefined;
    $scope.total_page_rank_results = 0;
  };

  $scope.search = function(lucene_based) {
    $scope.overlaps = 0;
    let params = {};
    params.query = $scope.search_query;
    params.lucene_based = lucene_based;
    if(lucene_based) {
      $scope.lucene_search_term = $scope.search_query;
      params.start = $scope.lucene_start;
      params.rows = $scope.lucene_rows;
    }
    else {
      $scope.page_rank_search_term = $scope.search_query;
      params.start = $scope.page_rank_start;
      params.rows = $scope.page_rank_rows;
    }

    if($scope.search_query) {
      $http({
        url: '/solr/search',
        method: 'GET',
        params: params
      }).then(function success(response) {
        try {
          let results = response.data.response.docs;
          let resultsObj = {};
          for(let i = 0; i < results.length; i++) {
            resultsObj[results[i].id] = {};
            resultsObj[results[i].id]['data'] = results[i];
            if(lucene_based && $scope.page_rank_search_results[results[i].id]) {
              assignStyle(resultsObj[results[i].id], $scope.page_rank_search_results[results[i].id], colors[i % colors.length])
            }
            if(!lucene_based && $scope.lucene_search_results[results[i].id]) {
              assignStyle(resultsObj[results[i].id], $scope.lucene_search_results[results[i].id], colors[i % colors.length])
            }
          }
          if(lucene_based) {
            $scope.lucene_search_results = resultsObj;
            $scope.total_lucene_results = response.data.response.numFound;
          }
          else {
            $scope.page_rank_search_results = resultsObj;
            $scope.total_page_rank_results = response.data.response.numFound;
          }
        } catch(e) {
          console.error(e);
        }
      }, function error(response) {
        console.error(response);
      });
    }
  };
}).
directive('resultsTable', function() {
  return {
    restrict: 'E',
    scope: {
      search_results: '=results',
      overlaps: '=overlaps',
      algo_desc: '@algoDesc'
    },
    templateUrl: 'results-table.html',
    controller: ['$scope', function ResultsTableController($scope) {
      $scope.isArray = angular.isArray;
      $scope.isObjectEmpty = function(obj) {
        return Object.keys(obj).length === 0;
      };
      $scope.showDetails = function(result) {
        result.details = !result.details;
      };
    }]
  };
});