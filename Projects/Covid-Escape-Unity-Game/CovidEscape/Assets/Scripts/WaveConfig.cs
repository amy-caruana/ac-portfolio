using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(menuName = "Infected Wave Config")]
public class WaveConfig : ScriptableObject
{
    //the infected that will spawn in this wave
    [SerializeField] GameObject infectedPrefab;

    //the path that the wave will follow
    [SerializeField] GameObject pathPrefab;

    //time between each infected Spawn
    [SerializeField] float timeBetweenSpawns = 0.5f;

    //random time difference between spawns
    [SerializeField] float spawnRandomFactor = 0.3f;

    //number of infected in the Wave
    [SerializeField] int numberOfInfected = 3;

    //the speed of the infected
    [SerializeField] float infectedMoveSpeed = 2f;

    public static object GameObject { get; internal set; }

    public GameObject GetInfectedPrefab()
    {
        return infectedPrefab;
    }

    public List<Transform> GetWaypoints()
    {
        //each wave can have different waypoints
        var waveWaypoints = new List<Transform>();

        //access pathPrefab and for each child
        //add it to the List waveWayPoints
        foreach (Transform child in pathPrefab.transform)
        {
            waveWaypoints.Add(child);
        }

        return waveWaypoints;
    }
    public float GetTimeBetweenSpawns()
    {
        return timeBetweenSpawns;
    }

    public float GetSpawnRandomFactor()
    {
        return spawnRandomFactor;
    }

    public float GetNumberOfInfected()
    {
        return numberOfInfected;
    }

    public float GetInfectedMoveSpeed()
    {
        return infectedMoveSpeed;
    }




    // Start is called before the first frame update
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {

    }
}
