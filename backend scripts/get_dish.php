<?php
    if (isset($_POST["date"])){
        $date = $_POST["date"];
        $user_id = intval($_POST["user_id"]);

        $mysqli = new mysqli("localhost","f0653156_JournalUser","ThisIsJournalDB","f0653156_FoodJournalRemote");

        if ($mysqli->connect_error){
            die("Connection failed: " . $mysqli->connect_error);
        }

        $stmt = $mysqli->prepare("SELECT local_id, dish, mass, calories, calories, eating, time FROM VALUES WHERE date=? AND user_id=? ORDER BY eating ASC");
        $stmt->bind_param("si", $date, $user_id);

        $stmt->execute();

        $result = $stmt->get_result();
        $row = $result->fetch_assoc();

        $data = array();
        $current_eating = "";

        for ($i = 0; $i < count($row); $i++){
            
        }

        echo "200";
    }
    else {
        echo "400";
    }
?>