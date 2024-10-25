using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HumanController : MonoBehaviour
{
    Animator anim;
    private Rigidbody2D body;

    // Start is called before the first frame update
    void Start()
    {
        anim = GetComponent<Animator>();
        body = GetComponent<Rigidbody2D>();
    }

    // Update is called once per frame
    void Update()
    {
        body.velocity = new Vector2(0f,0f);
        
        if(Input.GetKey(KeyCode.D))
        {
            anim.SetBool("human-right", true);
            body.velocity = new Vector2(1f,0f);
        }
        else
        {
            anim.SetBool("human-right", false);
        }
        
        if(Input.GetKey(KeyCode.A))
        {
            anim.SetBool("human-left", true);
            body.velocity = new Vector2(-1f,0f);
        }
        else
        {
            anim.SetBool("human-left", false);
        }

        if(Input.GetKey(KeyCode.W))
        {
            anim.SetBool("human-up", true);
            body.velocity = new Vector2(0f,1f);
        }
        else
        {
            anim.SetBool("human-up", false);
        }

        if(Input.GetKey(KeyCode.S))
        {
            anim.SetBool("human-down", true);
            body.velocity = new Vector2(0f,-1f);
        }
        else
        {
            anim.SetBool("human-down", false);
        }  
    }
}
