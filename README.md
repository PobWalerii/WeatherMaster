# WeatherMaster


class Location : ArrayList<LocationItem>()

class LocationItem(
@SerializedName("country")
val country: String,
@SerializedName("lat")
val lat: Double,
@SerializedName("local_names")
val localNames: LocalNames,
@SerializedName("lon")
val lon: Double,
@SerializedName("name")
val name: String,
@SerializedName("state")
val state: String
)

data class LocalNames(
val af: String,
val al: String,
val ar: String,
val az: String,
val be: String,
val br: String,
val ca: String,
val cs: String,
val cu: String,
......
)


//response: LocationItem
val localNames = response.localNames
var city: String? = localNames.javaClass.declaredFields
.firstOrNull { it.name == languageCode }
?.let {
it.isAccessible = true
it.get(localNames) as? String
}
if(city.isNullOrEmpty()) {
city = response.name
}