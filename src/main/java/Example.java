package org.potassco.clingo;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.Callback;
import com.sun.jna.IntegerType;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class Example {
    public static class SizeT extends IntegerType {
        private static final long serialVersionUID = 1L;
        public SizeT() { this(0); }
        public SizeT(long value) { super(Native.SIZE_T_SIZE, value, true); }
    }

    public static class SizeTByReference extends ByReference {
        public SizeTByReference() {
            this(0);
        }

        public SizeTByReference(long value) {
            super(Native.SIZE_T_SIZE);
            setValue(value);
        }

        public void setValue(long value) {
            Pointer p = getPointer();
            switch (Native.SIZE_T_SIZE) {
            case 2:
                p.setShort(0, (short)value);
                break;
            case 4:
                p.setInt(0, (int)value);
                break;
            case 8:
                p.setLong(0, value);
                break;
            default:
                throw new RuntimeException("Unsupported size: " + Native.SIZE_T_SIZE);
            }
        }

        public long getValue() {
            Pointer p = getPointer();
            switch (Native.SIZE_T_SIZE) {
            case 2:
                return p.getShort(0) & 0xFFFFL;
            case 4:
                return p.getInt(0) & 0xFFFFFFFFL;
            case 8:
                return p.getLong(0);
            default:
                throw new RuntimeException("Unsupported size: " + Native.SIZE_T_SIZE);
            }
        }
    }

    public static class PartT extends Structure {
        public String name;
        public Pointer params;
        public SizeT size;
        protected List<String> getFieldOrder() {
            return Arrays.asList("name", "params", "size");
        }
    }

    public static abstract class SolveEventCallbackT implements Callback {
        public abstract boolean call(int type, Pointer event, Pointer goon);

        public boolean callback(int type, Pointer event, Pointer data, Pointer goon) {
            return call(type, event, goon);
        }
    }

    public interface CLibrary extends Library {
        CLibrary INSTANCE = Native.load(("clingo"), CLibrary.class);

        void printf(String format, Object... args);
        void clingo_version(IntByReference major, IntByReference minor, IntByReference patch);
        //bool clingo_control_new(char const *const * arguments, size_t arguments_size, clingo_logger_t logger, void *logger_data, int message_limit, clingo_control_t **control);
        boolean clingo_control_new(Pointer arguments, SizeT arguments_size, Pointer logger, Pointer logger_data, int message_limit, PointerByReference control);
        //void clingo_control_free(clingo_control_t *control);
        void clingo_control_free(Pointer control);
        //bool clingo_control_add(clingo_control_t *control, char const *name, char const * const * parameters, size_t parameters_size, char const *program);
        boolean clingo_control_add(Pointer control, String name, String[] parameters, SizeT parameters_size, String program);
        //bool clingo_control_ground(clingo_control_t *control, clingo_part_t const *parts, size_t parts_size, clingo_ground_callback_t ground_callback, void *ground_callback_data);
        boolean clingo_control_ground(Pointer control, PartT[] parts, SizeT parts_size, Pointer ground_callback, Pointer ground_callback_data);
        //bool clingo_control_solve(clingo_control_t *control, clingo_solve_mode_bitset_t mode, clingo_literal_t const *assumptions, size_t assumptions_size, clingo_solve_event_callback_t notify, void *data, clingo_solve_handle_t **handle);
        boolean clingo_control_solve(Pointer control, int mode, Pointer assumptions, SizeT assumptions_size, SolveEventCallbackT notify, Pointer data, PointerByReference handle);
        //bool clingo_solve_handle_get(clingo_solve_handle_t *handle, clingo_solve_result_bitset_t *result);
        boolean clingo_solve_handle_get(Pointer handle, IntByReference result);
        //bool clingo_solve_handle_close(clingo_solve_handle_t *handle);
        boolean clingo_solve_handle_close(Pointer handle);
        //bool clingo_model_symbols_size(clingo_model_t const *model, clingo_show_type_bitset_t show, size_t *size);
        boolean clingo_model_symbols_size(Pointer model, int show, SizeTByReference size);
        //bool clingo_model_symbols(clingo_model_t const *model, clingo_show_type_bitset_t show, clingo_symbol_t *symbols, size_t size);
        boolean clingo_model_symbols(Pointer model, int show, long[] symbols, SizeT size);
        //bool clingo_symbol_to_string_size(clingo_symbol_t symbol, size_t *size);
        boolean clingo_symbol_to_string_size(long symbol, SizeTByReference size);
        //bool clingo_symbol_to_string(clingo_symbol_t symbol, char *string, size_t size);
        boolean clingo_symbol_to_string(long symbol, byte[] string, SizeT size);
    }

    public static void main(String[] args) {
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        IntByReference patch = new IntByReference();
        CLibrary.INSTANCE.clingo_version(major, minor, patch);
        System.out.printf("clingo version: %d.%d.%d\n", major.getValue(), minor.getValue(), patch.getValue());

        PointerByReference ctl = new PointerByReference();
        CLibrary.INSTANCE.clingo_control_new(null, new SizeT(0), null, null, 20, ctl);
        // add a program
        CLibrary.INSTANCE.clingo_control_add(ctl.getValue(), "base", null, new SizeT(0), "a. b.");
        // ground it
        PartT[] parts = new PartT [1];
        parts[0] = new PartT();
        parts[0].name = "base";
        parts[0].params = null;
        parts[0].size = new SizeT(0);
        CLibrary.INSTANCE.clingo_control_ground(ctl.getValue(), parts, new SizeT(1), null, null);
        // solve it
        SolveEventCallbackT cb = new SolveEventCallbackT() {
            public boolean call(int type, Pointer event, Pointer goon) {
                if (type == 0) {
                    SizeTByReference num = new SizeTByReference();
                    CLibrary.INSTANCE.clingo_model_symbols_size(event, 2, num);
                    System.out.printf("model: %d\n", num.getValue());
                    long[] symbols = new long [(int)num.getValue()];
                    CLibrary.INSTANCE.clingo_model_symbols(event, 2, symbols, new SizeT(num.getValue()));
                    System.out.print("ANSWER:");
                    for (int i = 0; i < num.getValue(); ++i) {
                        SizeTByReference len = new SizeTByReference();
                        CLibrary.INSTANCE.clingo_symbol_to_string_size(symbols[i], len);
                        byte[] str = new byte[(int)len.getValue()];
                        CLibrary.INSTANCE.clingo_symbol_to_string(symbols[i], str, new SizeT(len.getValue()));
                        System.out.format(" %s", new String(str));
                    }
                    System.out.println();

                }
                return true;
            }
        };
        PointerByReference hnd = new PointerByReference();
        CLibrary.INSTANCE.clingo_control_solve(ctl.getValue(), 0, null, new SizeT(0), cb, null, hnd);
        IntByReference res = new IntByReference();
        CLibrary.INSTANCE.clingo_solve_handle_get(hnd.getValue(), res);
        CLibrary.INSTANCE.clingo_solve_handle_close(hnd.getValue());
        // clean up
        CLibrary.INSTANCE.clingo_control_free(ctl.getValue());
    }
}
