package com.example.mdpgroup35.Algo;

import java.util.Arrays;

public class PathPlanner {

    public double min_cost;
    public int[] path;

    public PathPlanner(int len){
        this.min_cost=Double.MAX_VALUE;
        this.path= new int[len];
    }

    public void tsp(double[][] w,boolean[] vis,int cur,int cnt,int n, double cost,int[] ans){
        if (cnt==n){
            if (cost<this.min_cost){
                this.min_cost=cost;
                this.path=ans;
                return;
            }
        }
        for(int i=0;i<n;i++)
        {
            if(vis[i]==false) {
                if (cost+w[cur][i]>this.min_cost){
                    continue;
                }
                vis[i]=true;

                //so that calls on same level aren't affected by those made by deeper level
                int[] new_ans=new int[n];

                for(int k=0;k<cnt;k++)
                {
                    new_ans[k]=ans[k];
                }

                new_ans[cnt]=i;
                tsp(w,vis,i,cnt+1,n,cost+w[cur][i],new_ans);
                //so that calls on same level aren't affected by those made by deeper level
                //to reset to conditions prior to recursive call
                vis[i]=false;
            }
        }

    }

    public void plan(double[][]w,int n){
        boolean[] vis=new boolean[n];
        vis[0]=true;
        int[] ans= new int[n];
        tsp(w,vis,0,1,n,0,ans);
        //can return path and min cost using a Pair or can just access them from the class
        System.out.println("cost "+String.valueOf(this.min_cost)+" path: "+ Arrays.toString(this.path));

    }
}
