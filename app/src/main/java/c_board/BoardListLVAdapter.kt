package c_board

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.fridgeguardian.R
import utils.FBAuth

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

        //view 재활용때문에 주석 처리함
       // if(view==null){

            view=LayoutInflater.from(parent?.context).inflate(R.layout.board_list_item,parent,false)

       // }

        val itemLinearLayoutView = view?.findViewById<LinearLayout>(R.id.c_p_itemView)

        if(boardList[position].uid.equals(FBAuth.getUid())){
            itemLinearLayoutView?.setBackgroundColor(Color.parseColor("#ffe5ec"))
            //내가 쓴 게시글은 연핑크로 표시되어서 보임
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