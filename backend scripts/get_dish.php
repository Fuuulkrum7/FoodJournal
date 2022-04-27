<?php
    if (isset($_POST["date"])){
        $date = $_POST["date"];
        $user_id = intval($_POST["user_id"]);

        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }

        $stmt = $mysqli->prepare("SELECT local_id, dish, mass, calories, eating, time FROM VALUES WHERE date=? AND user_id=? ORDER BY eating ASC");
        $stmt->bind_param("si", $date, $user_id);

        $stmt->execute();

        $data = array();
        $dishes = array();
        $current_eating = "";

        $result = $stmt->get_result();

        if ($result->num_rows > 0){
            while ($row = $result->fetch_assoc()) {
                if ($current_eating == "") {
                    $current_eating = $row["eating"];
                }

                if ($current_eating != $row["eating"]) {
                    $data[intval($current_eating)] == $dishes;

                    unset($dishes);
                    $dishes = array();
                }

                $dishes["local_id"] = intval($row["local_id"]);
                $dishes["dish"] = $row["dish"];
                $dishes["mass"] = intval($row["mass"]);
                $dishes["calories"] = intval($row["calories"]);
                $dishes["time"] = $row["time"];
            }
        }        

        echo json_encode($data);
    }
    else {
        echo "400";
    }
?>