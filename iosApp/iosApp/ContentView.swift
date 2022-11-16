import SwiftUI
import shared

struct ContentView: View {
    
    let collage = TCCollage()
    
    @State var collageString = ""
    
    var body: some View {
        VStack {
            Text(collageString)
            Button("collage"){
                var s = ""
                var bitmaps: [TCBitmap] = []
                
                for _ in (0...10) {
                    let w = Int.random(in: (500...2000))
                    let h = Int.random(in: (500...2000))
                    let uuid = UUID().uuidString
                    bitmaps.append(TCBitmap(uuid: uuid, width: Int32(w), height: Int32(h)))
                }
                collage.doInit(bitmaps: bitmaps)
                guard let result = collage.collage(width: 1000.0, height: 1000.0, padding: 0.0) else {
                    return
                }
                bitmaps.forEach {
                    guard let rect = result.get(uuid: $0.uuid) else {
                        return
                    }
                    s.append("\(rect)\n")
                }
                collageString = s
            }
        }.animation(.easeInOut, value: collageString)
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
