using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public class InfectedSpawner : MonoBehaviour
{

    //a list of WaveConfigs
    [SerializeField] List<WaveConfig> waveConfigList;

    [SerializeField] bool looping = false;

    //Game always start from Wave 0
    int startingWave = 0;

    // Start is called before the first frame update
    IEnumerator Start()
    {
        do
        {
            //start coroutine that spawns all waves
            yield return StartCoroutine(SpawnAllWaves());
        }
        while (looping); // this is same thing as: while (looping == true)

    }

    //when calling Coroutine, specify which Wave we need to spawn infected from
    private IEnumerator SpawnAllInfectedInWave(WaveConfig waveToSpawn)
    {
        for (int infectedCount = 1; infectedCount <= waveToSpawn.GetNumberOfInfected(); infectedCount++)
        {
            //spawn the infected from waveConfig at the position specified by waveConfig waypoints
            var newInfected = Instantiate(
                    waveToSpawn.GetInfectedPrefab(),
                    waveToSpawn.GetWaypoints()[0].transform.position, Quaternion.identity);

            //the wave will be selected from here and the infected applied to it
            newInfected.GetComponent<InfectedMovement>().SetWaveConfig(waveToSpawn);

            //wait timeBetweenSpawns before spawning another infected
            yield return new WaitForSeconds(waveToSpawn.GetTimeBetweenSpawns());
        }
    }

    private IEnumerator SpawnAllWaves()
    {
        //loop all waves
        foreach (WaveConfig currentWave in waveConfigList)
        {
            //wait for all infected to spawn before going to the next wave
            yield return StartCoroutine(SpawnAllInfectedInWave(currentWave));
        }
    }
}
