package c_board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.fridgeguardian.R

class BoardListLVAdapter(val boardList : MutableList<BoardModel>):BaseAdapter(){
    override fun getCount(): Int {
       return boardList.size
    }

    override fun getItem(position: Int): Any {
       return boardList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if(view==null){

            view=LayoutInflater.from(parent?.context).inflate(R.layout.board_list_item,parent,false)

        }

        val title_rname = view?.findViewById<TextView>(R.id.titleArea_Rname)
        title_rname!!.text=boardList[position].title_Rname

        val content_ring = view?.findViewById<TextView>(R.id.contentArea_Ring)
        content_ring!!.text=boardList[position].content_Ring

        val content_rcon = view?.findViewById<TextView>(R.id.contentArea_Rcon)
        content_rcon!!.text=boardList[position].content_Rcon

        val time = view?.findViewById<TextView>(R.id.timeArea)
        time!!.text=boardList[position].time

        return view!!
    }

}