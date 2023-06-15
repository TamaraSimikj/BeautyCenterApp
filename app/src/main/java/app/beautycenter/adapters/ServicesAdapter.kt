package app.beautycenter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.beautycenter.R
import app.beautycenter.model.Service

class ServicesAdapter(private val services: List<Service>) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvServiceName: TextView = itemView.findViewById(R.id.tvServiceName)
        private val tvServiceDescription: TextView = itemView.findViewById(R.id.tvServiceDescription)
        private val tvServicePrice: TextView = itemView.findViewById(R.id.tvServicePrice)

        fun bind(service: Service) {
            tvServiceName.text = service.name
            tvServiceDescription.text = service.description
            tvServicePrice.text = "$${service.price}"
        }
    }
}
