using System.Collections;
using System.Collections.Generic;
using UnityEngine;
public class TetrisBlock : MonoBehaviour
{
    private float prevTime;
    private float waitTime = 0.5f;
    public static  int height = 20;
    public static  int width = 10;
    public Vector3 rotationPoint;
    private static Transform[,] grid = new Transform[width, height];
    void Start()
    {

    }
    void AddToGrid(){
      foreach(Transform square in transform){
        int roundedX = Mathf.RoundToInt(square.transform.position.x+0.5f);
        int roundedY = Mathf.RoundToInt(square.transform.position.y+0.5f);


        grid[roundedX, roundedY] = square;
      }
        return;
    }
    // Update is called once per frame
    void Update()
    {
      if(Input.GetKeyDown(KeyCode.LeftArrow)){
        transform.position += new Vector3(-1, 0, 0);
        if(!ValidMove()){
        transform.position += new Vector3(+1, 0, 0);
        }
      }else if(Input.GetKeyDown(KeyCode.RightArrow)){
        transform.position += new Vector3(1, 0, 0);
        if(!ValidMove()){
        transform.position += new Vector3(-1, 0, 0);
        }
      }else if(Input.GetKeyDown(KeyCode.UpArrow)){
        transform.RotateAround(transform.TransformPoint(rotationPoint), new Vector3(0,0,1), -90);
        if(!ValidMove()){
          transform.RotateAround(transform.TransformPoint(rotationPoint), new Vector3(0,0,1),-90);
        }
      }
      if(Time.time - prevTime >= (Input.GetKey(KeyCode.DownArrow)?waitTime/10:waitTime)){
        if(ValidMove()){
          transform.position += new Vector3(0, -1, 0);
        }else{
          AddToGrid();
          this.enabled = false;
          FindObjectOfType<SpawnTetromino>().NewTetromino();
        }
        prevTime = Time.time;
      }
    }
    bool ValidMove(){
      foreach(Transform square in transform){
        int roundedX = Mathf.RoundToInt(square.transform.position.x);
        int roundedY = Mathf.RoundToInt(square.transform.position.y);

        if(roundedX < 0f || roundedX>=width||roundedY <= 0f || roundedY>=height)
          return false;
        if(grid[roundedX, roundedY]!=null){
          Debug.Log("here");
          return false;
        }
      }
      return true;
    }
}
