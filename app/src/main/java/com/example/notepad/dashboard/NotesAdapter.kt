package com.example.notepad.dashboard



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.database.Note
import com.example.notepad.databinding.NoteRecyclerViewBinding

class NotesAdapter(val clickListener: NoteListener) : ListAdapter<Note,
        RecyclerView.ViewHolder>(NotesDiffCallBack())
{

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> {
                val note=getItem(position)
                holder.bind(clickListener, note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: NoteRecyclerViewBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: NoteListener, note: Note){
                binding.note=note
                binding.clickListener=clickListener
                binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup) : ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NoteRecyclerViewBinding.inflate(layoutInflater,parent,false)

                return ViewHolder(binding)
            }
        }
    }

}

class NoteListener(val clickListener: (note : Note) -> Unit){
    fun onClick(note: Note) = clickListener(note)
}

class NotesDiffCallBack() : DiffUtil.ItemCallback<Note>(){
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem==newItem
    }
}