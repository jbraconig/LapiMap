package com.intic.lapi.graphic

import android.graphics.Bitmap
import android.util.Log
import kotlin.math.abs
import kotlin.math.min

class PathFinding() {
    private val MOVE_STRAIGHT_COST = 10
    private val MOVE_DIAGONAL_COST = 14
    private val openList = arrayListOf<PathNode>()
    private val closeList = arrayListOf<PathNode>()
    val cellSize = 10
    private lateinit var spritesSheet:SpritesSheet
    var grid:Grid<PathNode> = Grid(1,1,1){x, y -> PathNode(x, y)}
    fun loadMap(bitmap: Bitmap){
        grid = Grid(bitmap.width,bitmap.height,cellSize){x, y -> PathNode(x, y)}
        spritesSheet = SpritesSheet(bitmap,cellSize)
        for(x in 0 until grid.w){
            for(y in 0 until grid.h){
                val pathNode = grid.getGridObject(x,y)
                pathNode.isEnabled = spritesSheet.isEnabled(x,y)
            }
        }
    }
    fun findPath(startX:Int, startY:Int,endX:Int,endY:Int): MutableList<PathNode> {
        openList.clear()
        closeList.clear()
        val startNode = grid.getGridObject(startX,startY)
        val endNode = grid.getGridObject(endX,endY)
        openList.add(startNode)
        for(x in 0 until grid.w){
            for(y in 0 until grid.h){
                val pathNode = grid.getGridObject(x,y)
                pathNode.gCost = Int.MAX_VALUE
                pathNode.cameFromNode = null
                pathNode.calculateFCost()
            }
        }
        startNode.hCost = 0
        startNode.hCost = calculateDistanceCost(startNode,endNode)
        startNode.calculateFCost()
        while (openList.size > 0){
            val currentNode = getLowestFCostNode(openList)
            if(currentNode == endNode){
                //Reached final node
                return calculatePath(endNode)
            }
            openList.remove(currentNode)
            closeList.add(currentNode)
            for(neighbourNode in getNeighbourList(currentNode)){
                if(closeList.contains(neighbourNode)) continue
                if(!neighbourNode.isEnabled){
                    closeList.add(neighbourNode)
                    continue
                }
                val tentativeGCost = currentNode.gCost + calculateDistanceCost(currentNode,neighbourNode)
                if(tentativeGCost< neighbourNode.gCost){
                    neighbourNode.cameFromNode = currentNode
                    neighbourNode.gCost = tentativeGCost.toInt()
                    neighbourNode.hCost = calculateDistanceCost(neighbourNode,endNode).toInt()
                    neighbourNode.calculateFCost()
                    if(!openList.contains(neighbourNode)){
                        openList.add(neighbourNode)
                    }
                }

            }
        }
        return arrayListOf()

    }

    private fun getNeighbourList(currentNode:PathNode): ArrayList<PathNode> {
        val neighbourList = arrayListOf<PathNode>()
        if(currentNode.x -1 >= 0){
            //Left
            neighbourList.add(getNode(currentNode.x-1,currentNode.y))
            //Left Down
            if(currentNode.y -1 >= 0) neighbourList.add(getNode(currentNode.x-1,currentNode.y-1))
            //Left up
            if(currentNode.y + 1 < grid.w) neighbourList.add(getNode(currentNode.x -1,currentNode.y+1))
        }
        if(currentNode.x+1 < grid.w){
            //Right
            neighbourList.add(getNode(currentNode.x +1,currentNode.y))
            //Right Down
            if(currentNode.y -1 >= 0) neighbourList.add(getNode(currentNode.x + 1, currentNode.y - 1))
            //Right Up
            if(currentNode.y + 1 < grid.w) neighbourList.add(getNode(currentNode.x+1, currentNode.y+1))
        }
        //Down
        if(currentNode.y -1 >= 0) neighbourList.add(getNode(currentNode.x,currentNode.y-1))
        //Up
        if(currentNode.y +1 < grid.h) neighbourList.add(getNode(currentNode.x,currentNode.y+1))
        return neighbourList
    }

    private fun getNode(x: Int, y: Int): PathNode {
        return grid.getGridObject(x,y)!!
    }

    private fun calculatePath(end:PathNode): MutableList<PathNode> {
        val path = ArrayList<PathNode>()
        path.add(end)
        var next = end.cameFromNode
        while (next != null){
            path.add(next)
            next = next.cameFromNode
        }
        path.reverse()
        return path
    }

    private fun calculateDistanceCost(a: PathNode, b:PathNode): Int {
        val (xDistance,yDistance) = abs(a.x - b.x) to abs(a.y - b.y)
        val remaning = abs(xDistance - yDistance)
        return MOVE_DIAGONAL_COST * min(xDistance,yDistance) + MOVE_STRAIGHT_COST * remaning
    }

    private fun getLowestFCostNode(pathNodeList:ArrayList<PathNode>): PathNode {
        var lowestFCostNode = pathNodeList[0]
        for(pn in pathNodeList){
            if(pn.fCost < lowestFCostNode.fCost){
                lowestFCostNode = pn
            }
        }
        return lowestFCostNode
    }
}