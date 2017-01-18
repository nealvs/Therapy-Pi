angular.module('therapyui.machine-service', [])

.factory('Machine', function(Common) {
    var service = {};

    service.getStatus = function() {
        return Common.get('/status');
    };

    service.loadPatients = function() {
        return Common.get('/patientList');
    };
    service.loadPatient = function(id) {
        return Common.get('/patient/' + id);
    };

    service.createPatient = function(patient) {
        return Common.post('/createPatient', patient);
    };
    service.deletePatient = function(patientId) {
        return Common.post('/deletePatient', {'id': patientId});
    };

    service.startSession = function(patientId) {
        return Common.post('/startSession', {'patientId': patientId});
    };
    service.stopSession = function() {
        return Common.post('/stopSession', {});
    };
    service.loadSession = function(id) {
        return Common.get('/session/' + id);
    };

    service.calibrate = function(id) {
        return Common.post('/settings/calibrate');
    };

    service.updateJoystick = function(value) {
        return Common.post('/updateJoystick', {'value': value});
    };

    service.reset = function(value) {
        return Common.post('/reset', {});
    };

    return service;
});
