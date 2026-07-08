package com.example.alethea.MejiaRachel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.R

data class PrestamoPendiente(
    val id: Int,
    val usuarioNombre: String,
    val libroNombre: String,
    val fechaSolicitada: String,
    val libroId: Int,
    val usuarioId: Int
)

class PrestamoPendienteAdapter(
    private val prestamos: List<PrestamoPendiente>,
    private val onAceptar: (PrestamoPendiente) -> Unit,
    private val onRechazar: (PrestamoPendiente) -> Unit
) : RecyclerView.Adapter<PrestamoPendienteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val tvLibro: TextView = view.findViewById(R.id.tvLibro)
        val tvFechaSolicitada: TextView = view.findViewById(R.id.tvFechaSolicitada)
        val btnAceptar: TextView = view.findViewById(R.id.btnAceptar)
        val btnRechazar: TextView = view.findViewById(R.id.btnRechazar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prestamo_pendiente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = prestamos[position]
        holder.tvUsuario.text = p.usuarioNombre
        holder.tvLibro.text = p.libroNombre
        holder.tvFechaSolicitada.text = p.fechaSolicitada
        holder.btnAceptar.setOnClickListener { onAceptar(p) }
        holder.btnRechazar.setOnClickListener { onRechazar(p) }
    }

    override fun getItemCount(): Int = prestamos.size
}