$(document).ready( function () {

    var userApi = $('meta[name=userapi]').attr("content");
    var api = $('meta[name=api]').attr("content");

    var table = $('#user_table').DataTable({
        "processing": true,
        "serverSide": false,
        "ajax": {
            "url": userApi,
            "type": "POST",
            "contentType": "application/json",
            "data": function ( d ) {
                return JSON.stringify( d );
            }
        },
        columns: [
             {
                "className":      'details-control',
                "orderable":      false,
                "data":           '',
                "defaultContent": '<button type="button" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> Edit</button>'
            },
            { "data": "username"},
            { "data": "name"},
            { "data": "email"}
        ]
    });

   $('#states_table').DataTable({
       "paging": false,
       "info": false
   });

   $('#districts_table').DataTable({
       "paging": false,
       "info": false
   });

   $('#admin_table').DataTable({
       "paging": false,
       "info": false,
       "searching": false
   });

   $('#user_table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );

        window.location.href = 'ldap/user/' + row.data().username;
   });

    // replace districts on state change
    $("#state").change(function() {
        var state = $(this).val();
        var districtDropdown = $("#district");

        var stateRolesTable = $('#states_table').DataTable();
        stateRolesTable.clear();

        var districtRolesTable = $('#districts_table').DataTable();
        districtRolesTable.clear();

        var adminTable = $("#admin_table").DataTable();
        adminTable.clear().draw();

        if (state === "National level") {
            districtDropdown.prop("disabled", true);

            adminTable.row.add(([buildAdminRadioButtons("UA"), buildAdminRadioButtons("V"), buildAdminRadioButtons("N")])).draw();

            $.get(api + '/states' , function(data) {
                $.each(data.Item, function(index, value) {
                    stateRolesTable.row.add([value.$, buildStateRadioButtons(value.$, 'UA'), buildStateRadioButtons(value.$, 'V'), buildStateRadioButtons(value.$, 'N')]);
                });

                stateRolesTable.draw();
            });

            $.get(api + '/districts' , function(data) {
                $.each(data.data, function(index, value) {
                    districtRolesTable.row.add([value.district, buildDistrictRadioButtons(value.state, value.district, 'UA'), buildDistrictRadioButtons(value.state, value.district, 'V'), buildDistrictRadioButtons(value.state, value.district, 'N')]);
                });

                districtRolesTable.draw();
            });

        } else {
            districtDropdown.prop("disabled", false);
            stateRolesTable.row.add([state, buildStateRadioButtons(state, 'UA'), buildStateRadioButtons(state, 'V'), buildStateRadioButtons(state, 'N')]).draw();

            $.get(api + '/districts/' + state, function(data) {
                districtDropdown.empty(); // remove old options

                $.each(data.Item, function(index, value) {
                  console.log("Found district: " + value.$);
                  districtDropdown.append($("<option>", {
                    value: value.$,
                    text: value.$
                  }));
                  if (value.$ !== "State level") {
                    districtRolesTable.row.add([value.$, buildDistrictRadioButtons(state, value.$, 'UA'), buildDistrictRadioButtons(state, value.$, 'V'), buildDistrictRadioButtons(state, value.$, 'N')]);
                  }
                });

                districtRolesTable.draw();
            });
        }
    });

    $("#district").change(function() {
        var state = $("#state").val();
        var district = $(this).val();
        var adminTable = $("#admin_table").DataTable();

        if (district === "State level") {
            $("#state").trigger("change"); // Reloads state role and district roles belonging to that state
        } else {
            var stateRolesTable = $('#states_table').DataTable();
            stateRolesTable.clear().draw();

            adminTable.clear().draw();

            var districtRolesTable = $('#districts_table').DataTable();
            districtRolesTable.clear();
            districtRolesTable.row.add([district, buildDistrictRadioButtons(state, district, 'UA'), buildDistrictRadioButtons(state, district, 'V'), buildDistrictRadioButtons(state, district, 'N')]).draw();
        }
    });

    if ($("#state").val() === "National level") {
        $("#district").prop("disabled", true);
    }
} );

function buildAdminRadioButtons(role) {
    var disabled = (role === "UA" && !adminRights.masterAdmin) ? "disabled" : "";
    return '<input type="radio" ' + disabled + ' name="nationalRole" value="' + role + '" />';
}

function buildStateRadioButtons(state, role) {
    var disabled = (role === "UA" && !adminRights.nationalAdmin) ? "disabled" : "";
    return '<input type="radio" ' + disabled + ' name="role_state_' + state + '" value="' + role + '" />';
}

function buildDistrictRadioButtons(state, district, role) {
    var disabled = (role === "UA" && !adminRights.nationalAdmin && !($.inArray(state, adminRights.states) > -1)) ? "disabled" : "";
    return '<input type="radio" ' + disabled + ' name="role_district_' + state + '__' + district + '" value="' + role + '" />';
}

function deleteUser(username) {
    var confirmation = confirm("Do you really want to delete this user? This operation is irreversible.")
    if (confirmation) {
        $.post(username + '/delete', function(data) {
            window.location.href = 'nms-users/ldap';
        });
    }
}