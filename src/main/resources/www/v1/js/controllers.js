angular.module('therapyui.controllers', ['ionic'])

.controller('AppCtrl', function($scope, $ionicModal, $timeout) {

  $scope.$on('$ionicView.enter', function(e) {
      console.log("App Started");

  });

})

.controller('PatientsCtrl', function($scope, Machine, $location, $ionicPopup, $ionicListDelegate) {
    $scope.$on('$ionicView.enter', function(e) {
    });
    $scope.form = {search: ""};
    $scope.patients = [];
    $scope.newPatient = {firstName: "", lastName: ""};

    $scope.loadPatients = function() {
        Machine.loadPatients().then(function(response) {
            $scope.patients.list = response.data.patients;
            console.log("Patients: " + $scope.patients.list.length);
            try {
              $scope.$digest();
            } catch(ex) {}
        });
    };

    $scope.clearSearch = function() {
        console.log('Clear Search');
        $scope.form.search = "";
        $ionicListDelegate.closeOptionButtons();
    };

    $scope.patientSelected = function() {
        $ionicListDelegate.closeOptionButtons();
    };

    $scope.showPatient = function(patient) {
        if($scope.form.search && patient) {
            var modifiedSearchString = $scope.form.search.replace(/\W/g, ' ');
            var parts = modifiedSearchString.split(" ");
            var containsAllParts = true;
            for(p=0; p<parts.length; p++) {
                part = parts[p].toLowerCase().trim();
                if(part && patient.firstName.toLowerCase().indexOf(part) < 0 && patient.lastName.toLowerCase().indexOf(part) < 0) {
                    containsAllParts = false;
                    break;
                }
            }
            return containsAllParts;
        }
        return true;
    };

    $scope.addPatient = function() {
        $scope.newPatient.firstName = "";
        $scope.newPatient.lastName = "";
        $scope.showNewPatientForm = true;
    };

    $scope.cancel = function() {
        $scope.newPatient.firstName = "";
        $scope.newPatient.lastName = "";
        $scope.showNewPatientForm = false;
    };

    $scope.createPatient = function() {
        if($scope.newPatient.firstName && $scope.newPatient.lastName) {
            Machine.createPatient($scope.newPatient)
            .success(function(response) {
                console.log(JSON.stringify(response));
                $scope.showNewPatientForm = false;
                $scope.loadPatients();
                $location.path("/app/patient/" + response.id);
            }).error(function(response) {
                $ionicPopup.alert({
                   title: 'Error',
                   template: 'Error adding patient'
                });
            });
        } else {
            $ionicPopup.alert({
               title: 'Missing Data',
               template: 'Please provide a First Name and Last Name'
            });
        }
    };

     $scope.deletePatient = function(patient) {

          var confirmPopup = $ionicPopup.confirm({
              title: 'Delete Patient',
              template: 'Are you sure you want to delete this patient?'
          });

          confirmPopup.then(function(res) {
              if(res) {
                  Machine.deletePatient(patient.id)
                     .success(function(response) {
                          console.log(JSON.stringify(response));
                          $scope.loadPatients();
                          $ionicListDelegate.closeOptionButtons();
                     }).error(function(response) {
                         $ionicPopup.alert({
                            title: 'Error',
                            template: 'Error deleting patient'
                         });
                     });
              } else {
                  // No
                  $ionicListDelegate.closeOptionButtons();
              }
          });
    };

    $scope.loadPatients();
})

.controller('PatientCtrl', function($scope, $state, $stateParams, Machine) {
    $scope.$on('$ionicView.enter', function(e) {
        console.log("Patient..." + JSON.stringify($stateParams));
    });
    $scope.patient = {};
    $scope.loadPatient = function() {
        Machine.loadPatient($stateParams.patientId).then(function(response) {
            console.log(JSON.stringify(response.data));
            $scope.patient = response.data.patient;
        });
    };
    $scope.startNewSession = function() {
        console.log("startNewSession: " + $stateParams.patientId);
        Machine.startSession($stateParams.patientId)
          .success(function(response) {
                console.log("startSession: " + JSON.stringify(response));
                $state.go("app.current");
          }).error(function(response) {
               $ionicPopup.alert({
                  title: 'Error',
                  template: 'Error starting new session'
               });
          });
    };
    $scope.loadPatient();
})

.controller('SessionCtrl', function($scope, $stateParams, Machine) {
    $scope.$on('$ionicView.enter', function(e) {
        console.log("Session..." + JSON.stringify($stateParams));
    });
    $scope.session = {};
    $scope.loadSession = function() {
        Machine.loadSession($stateParams.sessionId).then(function(response) {
            console.log(JSON.stringify(response.data));
            $scope.session = response.data.session;
        });
    };
    $scope.loadSession();
})

.controller('SettingsCtrl', function($scope, $stateParams, $interval, Machine) {
    $scope.$on('$ionicView.enter', function(e) {
        console.log("Settings..." + JSON.stringify($stateParams));
    });
    $scope.machine = {};
    $scope.running = true;
      var timer = $interval(function() {
          if($scope.running) {
            Machine.getStatus().then(function(response) {
                $scope.machine = response.data;
            });
          }
      }, 500);

      $scope.calibrate = function() {
          console.log("calibrate");
          Machine.calibrate()
            .success(function(response) {
                 $scope.machine = response.data;
            }).error(function(response) {
                 $ionicPopup.alert({
                    title: 'Error',
                    template: 'Error calibrating angle voltage'
                 });
            });
      };

      $scope.$on('$ionicView.leave', function(e) {
          console.log("Leaving settings...");
          $scope.running = false;
      });
})

.controller('CurrentSessionCtrl', function($scope, $state, $interval, Machine) {

  $scope.machine = {};
  $scope.machine.joystick = 0;

  $scope.$on('$ionicView.enter', function(e) {
      console.log("Demo start...");
      $scope.running = true;
      var timer = $interval(function() {
          if($scope.running) {
            Machine.getStatus().then(function(response) {
                if($scope.machine) {
                  response.data.joystickSlider = $scope.machine.joystickSlider;
                }
                $scope.machine = response.data;
            });
          }
      }, 500);
  });

  $scope.$on('$ionicView.leave', function(e) {
      console.log("Leaving current...");
      $scope.running = false;
  });

  $scope.stopSession = function() {
      console.log("stop session");
      Machine.stopSession()
      .success(function(response) {
            console.log("stopSession: " + JSON.stringify(response));
            $state.go("app.patient", {'patientId' : $scope.machine.session.patient.id });
      }).error(function(response) {
           $ionicPopup.alert({
              title: 'Error',
              template: 'Error stopping session'
           });
      });
  };

  $scope.reset = function() {
      Machine.reset().then(function(response) {
          $scope.machine = response.data;
      });
  };

  $scope.updateJoystick = function() {
      Machine.updateJoystick($scope.machine.joystickSlider / 100).then(function(response) {
          if($scope.machine) {
             response.data.joystickSlider = $scope.machine.joystickSlider;
          }
          $scope.machine = response.data;
      });
  };

})


