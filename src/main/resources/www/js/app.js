angular.module('therapyui', ['ui.router', 'ng-virtual-keyboard', 'therapyui.controllers', 'therapyui.machine-service', 'therapyui.common'])

.run(function() {
    console.log("Starting therapypi app")

})

.config(function($stateProvider, $urlRouterProvider, $httpProvider) {
  $stateProvider
    .state('app', {
      url: '/app',
      abstract: true,
      templateUrl: 'templates/menu.html', 
      controller: 'AppCtrl'
    })
    .state('app.current', {
        url: '/current',
        views: {
          'menuContent': {
            templateUrl: 'templates/current.html',
            controller: 'CurrentSessionCtrl'
          }
        }
    })
    .state('app.software', {
        url: '/software',
        views: {
          'menuContent': {
            templateUrl: 'templates/software.html',
            controller: 'SoftwareCtrl'
          }
        }
    })
    .state('app.settings', {
          url: '/settings',
          views: {
            'menuContent': {
              templateUrl: 'templates/settings.html',
              controller: 'SettingsCtrl'
            }
          }
      })
    .state('app.patients', {
        url: '/patients',
        views: {
          'menuContent': {
            templateUrl: 'templates/patients.html',
            controller: 'PatientsCtrl'
          }
        }
    })
    .state('app.patient', {
      url: '/patient/:patientId',
      views: {
        'menuContent': {
          templateUrl: 'templates/patient.html',
          controller: 'PatientCtrl'
        }
      }
    })
    .state('app.session', {
        url: '/session/:sessionId',
        views: {
          'menuContent': {
            templateUrl: 'templates/session.html',
            controller: 'SessionCtrl'
          }
        }
      })
    ;

    // if none of the above states are matched, use this as the fallback
    $urlRouterProvider.otherwise('/app/patients');


    // Disable AJAX Caching
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
    $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';
    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';

})
.filter('secondsToDateTime', [function() {
    return function(seconds) {
        var d = new Date(0,0,0,0,0,0,0);
        d.setSeconds(seconds);
        return d;
    };
}]);
