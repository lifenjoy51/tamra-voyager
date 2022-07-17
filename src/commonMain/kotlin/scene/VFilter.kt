package scene

import com.soywiz.korag.DefaultShaders
import com.soywiz.korag.shader.Operand
import com.soywiz.korag.shader.Program
import com.soywiz.korag.shader.VarType
import com.soywiz.korag.shader.VertexShader
import com.soywiz.korge.render.BatchBuilder2D
import com.soywiz.korge.view.filter.ShaderFilter
import com.soywiz.korma.geom.*
import kotlin.math.abs


data class FilterParams(
    val width: Double,
    val height: Double,
)

class VFilter(
    val p: FilterParams,
) : ShaderFilter() {

    @Suppress("UNCHECKED_CAST")
    private fun Matrix3D.toVector(): Program.Vector {
        val opArray = data
            .map { Program.FloatLiteral(it) }
            .toTypedArray()
        return Program.Vector(VarType.Mat4, opArray as Array<Operand>)
    }

    private val vertexShader = VertexShader {
        DefaultShaders.apply {
            SET(v_Tex, a_Tex)
            SET(BatchBuilder2D.v_TexIndex, BatchBuilder2D.a_TexIndex)
            SET(BatchBuilder2D.v_ColMul, BatchBuilder2D.a_ColMul)
            SET(BatchBuilder2D.v_ColAdd, BatchBuilder2D.a_ColAdd)

            val perspective = Matrix3D()
                .setToPerspective(
                    90.degrees,
                    1f,
                    0.001f,
                    100f
                )
                .toVector()

            val view = Matrix3D()
                .lookAt(
                    // save.
                    eye = Vector3D(0f, -1f, 5f),
                    target = Vector3D(0f, 0f, 0f),
                    up = Vector3D(0f, 1f, 0f),
                )
                .toVector()

            // centering.
            val x = (a_Pos.x / p.width.lit) - 0.5f.lit
            val y = (a_Pos.y / p.height.lit) - 0.485f.lit

            val model = Matrix3D()
                .toVector() * vec4(x, -y, 0f.lit, 0.1f.lit)

            //SET(out, perspective * view * model)
            SET(out, view * model)
        }
    }

    val bpp = object : BaseProgramProvider() {
        override val vertex: VertexShader = vertexShader
    }

    override val programProvider: ProgramProvider = bpp

    fun Matrix3D.lookAt(
        eye: Vector3D,
        target: Vector3D,
        up: Vector3D
    ): Matrix3D {
        val z = Vector3D().sub(eye, target)
        if (z.length3Squared == 0f) z.z = 1f
        z.normalize()
        val x = Vector3D().cross(up, z)
        if (x.length3Squared == 0f) {
            when {
                abs(up.z) == 1f -> z.x += 0.0001f
                else -> z.z += 0.0001f
            }
            z.normalize()
            x.cross(up, z)
        }
        x.normalize()
        val y = Vector3D().cross(z, x)
        return this.setRows(
            x.x, y.x, z.x, 0f,
            x.y, y.y, z.y, 0f,
            x.z, y.z, z.z, 0f,
            -x.dot(eye), -y.dot(eye), -z.dot(eye), 1f
        )
    }
}