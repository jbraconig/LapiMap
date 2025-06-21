package com.intic.lapi.graphic

class Sprites(private val lado:Int, val columna:Int, val fila:Int, val spritesSheet: SpritesSheet) {
    private val x:Int = columna * lado
    private val y:Int = fila * lado
    fun getValue(): Boolean {
        var contador = 0
        for(y in 0 until lado){
            for(x in 0 until lado){
                val pixel = spritesSheet.pixeles[(x+this.x)+(y+this.y) * spritesSheet.width]
                if(pixel == -1 || pixel == 0){
                    contador++
                }
            }
        }
        return !isEnabled(contador)
    }
    private fun isEnabled(contador:Int): Boolean {
        return contador < 65
    }
}