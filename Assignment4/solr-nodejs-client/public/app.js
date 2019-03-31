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
  let colors = ['#0000FF', '#00FF00', '#FFFF00', '#FF00FF',
                '#00FFFF', '#800000', '#FF0000', '#008080',
                '#808080', '#000080'];
  let assignStyle = function(obj1, obj2, color) {
      obj1.style = {"background-color": color};
      obj2.style = {"background-color": color};
  };
  $scope.clear = function() {
    $scope.lucene_search_results = {};
    $scope.page_rank_search_results = {};
  };
  $scope.search = function(lucene_based) {
    if($scope.search_query) {
      $http({
        url: '/solr/search',
        method: 'GET',
        params: {query: $scope.search_query, lucene_based: lucene_based}
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
          if(lucene_based)
            $scope.lucene_search_results = resultsObj;
          else
            $scope.page_rank_search_results = resultsObj;
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
      search_results: '=results'
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