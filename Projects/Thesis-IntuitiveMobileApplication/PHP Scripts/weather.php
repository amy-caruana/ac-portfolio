<?php
$period = $_POST['period'];     //0 = 2 days, 1 = 1 week, 2 = 2 weeks, 3 = 1 month
$sensor = $_POST['sensor'];     //0 = Temp, 1 = Humidity, 2 = Wind Direction, 3 = Wind Speed, 4 = Pressure
$trend = $_POST['trend'];       //0 = Historical, 1 = Prediction

$username = "";
$password = "";
$database = "";
$servername = "";

$tblstate = new \stdClass();

$tblstate->trend = $trend;
$tblstate->sensor = $sensor;
$tblstate->period = $period;


// Create connection
$conn = new mysqli($servername, $username, $password, $database);
// Check connection
if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
}

if (($period == '0') && ($sensor == '0'))
        $sql = "SELECT Time, Temperature FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 2 DAY";
if (($period == '1') && ($sensor == '0'))
        $sql = "SELECT Time, Temperature FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 7 DAY";
if (($period == '2') && ($sensor == '0'))
        $sql = "SELECT Time, Temperature FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 14 DAY";
if (($period == '3') && ($sensor == '0'))
        $sql = "SELECT Time, Temperature FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 30 DAY";

if (($period == '0') && ($sensor == '1'))
        $sql = "SELECT Time, humidity FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 2 DAY";
if (($period == '1') && ($sensor == '1'))
        $sql = "SELECT Time, humidity FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 7 DAY";
if (($period == '2') && ($sensor == '1'))
        $sql = "SELECT Time, humidity FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 14 DAY";
if (($period == '3') && ($sensor == '1'))
        $sql = "SELECT Time, Humidity FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 30 DAY";

if (($period == '0') && ($sensor == '2'))
        $sql = "SELECT Time, winddir FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 2 DAY";
if (($period == '1') && ($sensor == '2'))
        $sql = "SELECT Time, winddir FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 7 DAY";
if (($period == '2') && ($sensor == '2'))
        $sql = "SELECT Time, winddir FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 14 DAY";
if (($period == '3') && ($sensor == '2'))
        $sql = "SELECT Time, winddir FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 30 DAY";

if (($period == '0') && ($sensor == '3'))
        $sql = "SELECT Time, windspeed FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 2 DAY";
if (($period == '1') && ($sensor == '3'))
        $sql = "SELECT Time, windspeed FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 7 DAY";
if (($period == '2') && ($sensor == '3'))
        $sql = "SELECT Time, windspeed FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 14 DAY";
if (($period == '3') && ($sensor == '3'))
        $sql = "SELECT Time, windspeed FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 30 DAY";

if (($period == '0') && ($sensor == '4'))
        $sql = "SELECT Time, sealevelpressure FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 2 DAY";
if (($period == '1') && ($sensor == '4'))
        $sql = "SELECT Time, sealevelpressure FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 7 DAY";
if (($period == '2') && ($sensor == '4'))
        $sql = "SELECT Time, sealevelpressure FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 14 DAY";
if (($period == '3') && ($sensor == '4'))
        $sql = "SELECT Time, sealevelpressure FROM WEATHER_STATION WHERE DATE(Time) >= DATE(NOW()) - INTERVAL 30 DAY";

if ($trend == '1')
        $sql = "SELECT Time, hour, Temperature FROM PREDICTION";



//echo $sql;

$return_arr = array();
$result = $conn->query($sql);

$ld =  mysqli_num_rows($result);

if ($ld > 0) {
        $arrayindex = 0;
        while ($row = $result->fetch_assoc()) {
                if ($trend == '0') {
                        $row_array['id'] = $arrayindex;
                        $row_array['col1'] = $row['Time'];
                        $row_array['col2'] = $row['Temperature'];
                        array_push($return_arr, $row_array);
                } else {
                        $row_array['id'] = $arrayindex;
                        $row_array['col1'] = $row['hour'];
                        $row_array['col2'] = $row['Temperature'];
                        array_push($return_arr, $row_array);
                }
                $arrayindex++;
        }
        print_r('{"SensorData":' . json_encode($return_arr) . '}\n');
}

$conn->close();
?>
