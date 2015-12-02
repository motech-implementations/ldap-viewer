$(document).ready( function () {
    $('#user_table').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": "ldap/users",
            "type": "POST",
            "contentType": "application/json",
            "data": function ( d ) {
                return JSON.stringify( d );
            }
        },
        columns: [
            { "data": "username"},
            { "data": "name"},
            { "data": "email"},
            { "data": "admin"},
            { "data": "state"},
            { "data": "district"}
        ]
    });
} );

