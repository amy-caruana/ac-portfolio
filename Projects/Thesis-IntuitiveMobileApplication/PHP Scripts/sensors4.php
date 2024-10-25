<?php
$period = $_POST['period'];	//0 = 2 days, 1 = 1 week, 2 = 2 weeks, 3 = 1 month
$sensor = $_POST['sensor'];	//0 = Air, 1 = Soil
$serra = $_POST['serra'];	

$username = "";
$password = "";
$database = "";
$servername = "";

$tblstate = new \stdClass();

$tblstate->serra = $serra;
$tblstate->sensor = $sensor;
$tblstate->period = $period;


// Create connection
$conn = new mysqli($servername, $username, $password, $database);
// Check connection
if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
}

if (($period == '0') && ($sensor == '0'))
	$sql = "SELECT time_read, Hum_SHT, TempC_SHT FROM LHT65 WHERE DATE(time_read) >= DATE(NOW()) - INTERVAL 2 DAY";
	//$sql = "SELECT * FROM (select time_read, Hum_SHT, TempC_SHT from LHT65 order by time_read DESC LIMIT 10) as temp order by time_read ASC";
if (($period == '1') && ($sensor == '0'))
        $sql = "SELECT time_read, Hum_SHT, TempC_SHT FROM LHT65 WHERE DATE(time_read) >= DATE(NOW()) - INTERVAL 7 DAY";
if (($period == '2') && ($sensor == '0'))
        $sql = "SELECT time_read, Hum_SHT, TempC_SHT FROM LHT65 WHERE DATE(time_read) >= DATE(NOW()) - INTERVAL 14 DAY";
if (($period == '3') && ($sensor == '0'))
        $sql = "SELECT time_read, Hum_SHT, TempC_SHT FROM LHT65 WHERE DATE(time_read) >= DATE(NOW()) - INTERVAL 30 DAY";

if (($period == '0') && ($sensor == '1'))
        $sql = "SELECT time_read, Moisture_SOIL, TempC_SOIL, EC_SOIL FROM LSE01 WHERE DATE(time_read) >= DATE(NOW()) - INTERVAL 2 DAY";
if (($period == '1') && ($sensor == '1'))
        $sql = "SELECT time_read, Moisture_SOIL, TempC_SOIL, EC_SOIL FROM LSE01 WHERE DATE(time_read) >= DATE(NOW()) - INTERVAL 7 DAY";
if (($period == '2') && ($sensor == '1'))
        $sql = "SELECT time_read, Moisture_SOIL, TempC_SOIL, EC_SOIL FROM LSE01 WHERE DATE(time_read) >= DATE(NOW()) - INTERVAL 14 DAY";
if (($period == '3') && ($sensor == '1'))
        $sql = "SELECT time_read, Moisture_SOIL, TempC_SOIL, EC_SOIL FROM LSE01 WHERE DATE(time_read) >= DATE(NOW()) - INTERVAL 30 DAY";

//echo $sql;

$return_arr = array();
$result = $conn->query($sql);

$ld =  mysqli_num_rows($result);

if ($ld > 0) {
	$arrayindex = 0;
	while ($row = $result->fetch_assoc()) {
		if ($sensor == 0) {
			$row_array['id'] = $arrayindex;
			$row_array['col1'] = $row['time_read'];
			$row_array['col2'] = $row['Hum_SHT'];
			$row_array['col3'] = $row['TempC_SHT'];
			array_push($return_arr, $row_array);
		} else {
                        $row_array['id'] = $arrayindex;
                        $row_array['col1'] = $row['time_read'];
                        $row_array['col2'] = $row['Moisture_SOIL'];
                        $row_array['col3'] = $row['TempC_SOIL'];
			$row_array['col4'] = $row['EC_SOIL'];
                        array_push($return_arr, $row_array);
		}
        	$arrayindex++;
	}
	print_r('{"SensorData":' . json_encode($return_arr) . '}\n');
}

$conn->close();
?>
